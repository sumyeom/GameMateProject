package com.example.gamemate.domain.notification.service;

import com.example.gamemate.domain.notification.dto.NotificationResponseDto;
import com.example.gamemate.domain.notification.entity.Notification;
import com.example.gamemate.domain.notification.enums.NotificationType;
import com.example.gamemate.domain.notification.repository.EmitterRepository;
import com.example.gamemate.domain.notification.repository.NotificationRepository;
import com.example.gamemate.domain.user.entity.User;
import com.example.gamemate.global.config.RabbitMQConfig;
import com.example.gamemate.global.constant.ErrorCode;
import com.example.gamemate.global.exception.ApiException;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.stream.StreamInfo;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;

/**
 * 알림을 처리하는 서비스 클래스입니다.
 */
@Service
@Slf4j
public class NotificationService {

    private static final String STREAM_KEY = "notification_stream";
    private static final String GROUP_NAME = "notification-group";
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    private final NotificationRepository notificationRepository;
    private final EmitterRepository emitterRepository;
    private final RedisStreamService redisStreamService;
    private final RedisTemplate<String, Object> redisTemplate;

    private final RabbitTemplate rabbitTemplate;
    private final AmqpAdmin amqpAdmin;
    private final DirectExchange notificationExchange;

    public NotificationService(
            NotificationRepository notificationRepository,
            EmitterRepository emitterRepository,
            RedisStreamService redisStreamService,
            RabbitTemplate rabbitTemplate,
            AmqpAdmin amqpAdmin,
            DirectExchange notificationExchange,
            @Qualifier("notificationRedisTemplate") RedisTemplate<String, Object> redisTemplate) {
        this.notificationRepository = notificationRepository;
        this.emitterRepository = emitterRepository;
        this.redisStreamService = redisStreamService;
        this.redisTemplate = redisTemplate;
        this.rabbitTemplate = rabbitTemplate;
        this.amqpAdmin = amqpAdmin;
        this.notificationExchange = notificationExchange;
    }

    /**
     * 레디스 스트림의 스트림그룹을 생성합니다.
     */
    @PostConstruct
    public void init() {
        try {
            // 스트림이 존재하지 않으면 생성
            if (!Boolean.TRUE.equals(redisTemplate.hasKey(STREAM_KEY))) {
                redisTemplate.opsForStream()
                        .add(StreamRecords.newRecord()
                                .in(STREAM_KEY)
                                .ofMap(Collections.singletonMap("init", "init")));
            }

            // 그룹 정보 조회
            StreamInfo.XInfoGroups groups = redisTemplate.opsForStream().groups(STREAM_KEY);
            boolean groupExists = groups.stream()
                    .anyMatch(group -> GROUP_NAME.equals(group.groupName()));

            // 그룹이 없으면 생성
            if (!groupExists) {
                redisTemplate.opsForStream().createGroup(STREAM_KEY, GROUP_NAME);
            }
        } catch (Exception e) {
            log.error("스트림 초기화 중 오류 발생: {}", e.getMessage());
        }
    }

    /**
     * 알림을 생성합니다.
     * @param user 알림을 받는 사용자
     * @param type 알림 타입
     * @param relatedUrl 알림과 관련된 URL
     * @return Notification 생성된 알림
     */
    @Transactional
    public Notification createNotification(User user, NotificationType type, String relatedUrl) {
        Notification notification = new Notification(type.getContent(), relatedUrl, type, user);
        return notificationRepository.save(notification);
    }

    /**
     * 사용자의 모든 알림을 조회합니다.
     * @param loginUser 현재 인증된 사용자 정보
     * @return 로그인 한 사용자의 모든 알림이 담긴 List<NotificationResponseDto>
     */
    public List<NotificationResponseDto> findAllNotification(User loginUser) {
        return notificationRepository.findAllByReceiverId(loginUser.getId())
                .stream()
                .map(NotificationResponseDto::toDto)
                .toList();
    }

    /**
     * 단일 알림을 읽음 처리합니다.
     * @param loginUser 현재 인증된 사용자 정보
     * @param id 읽음 처리할 알림 id
     */
    @Transactional
    public void readNotification(User loginUser, Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.NOTIFICATION_NOT_FOUND));

        if (!Objects.equals(notification.getReceiver().getId(), loginUser.getId())) {
            throw new ApiException(ErrorCode.FORBIDDEN);
        } // 알림의 받는 사람과 로그인 한 유저가 다르면 예외 처리

        notification.updateIsRead(true);
    }

    /**
     * 로그인한 사용자의 읽지 않은 모든 알림을 읽음 처리합니다.
     * @param loginUser 현재 인증된 사용자 정보
     */
    @Transactional
    public void readAllNotification(User loginUser) {
        notificationRepository.updateUnreadNotificationToRead(loginUser.getId());
    }

    /**
     * SSE 연결을 구독합니다, RabbitMQ에 사용자 전용 Queue를 설정합니다.
     * @param loginUser 현재 인증된 사용자 정보
     * @return 사용자 연결정보가 담긴 SseEmitter
     */
    public SseEmitter subscribe(User loginUser) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

        String receiverId = loginUser.getId().toString();
        // 라우팅 키와 큐 이름은 사용자 ID를 기반으로 설정
        String routingKey = receiverId;
        String queueName = "notification.queue." + receiverId;

        try {
            // 1. 사용자 전용 큐 생성 (Durable: 영속성, Exclusive : 연결 종료 시 자동 삭제)
            // 'exclusive' 는 연결이 끊어지면 큐가 자동으로 삭제되도록 설정
            Queue queue = new Queue(queueName, true, true, true);

            // 2. Exchange와 큐를 바인딩(유저 ID를 라우팅 키로 사용)
            amqpAdmin.declareBinding(BindingBuilder
                    .bind(queue)
                    .to(notificationExchange) // 주입받은 DirectExchange 빈
                    .with(routingKey));
            log.info("사용자 전용 RabbitMQ Queue 및 Binding 생성 완료 : {}", queueName);


            // 연결 직후 더미 데이터를 보내 503 에러 방지
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("connected!"));

            // DB에서 가장 최근 읽지 않은 알림 1개만 조회
            notificationRepository.findTopByReceiverIdAndIsReadOrderByCreatedAtDesc(loginUser.getId(), false)
                    .ifPresent(notification -> {
                        try {
                            NotificationResponseDto notificationDto = NotificationResponseDto.toDto(notification);
                            emitter.send(SseEmitter.event()
                                    .name(notification.getType().name())
                                    .data(notificationDto));
                            log.debug("최근 알림 전송 - ID: {}", notification.getId());
                        } catch (IOException e) {
                            log.error("알림 전송 실패 - ID: {} - 에러: {}", notification.getId(), e.getMessage());
                        }
                    });

        } catch (IOException e) {
            log.error("SSE 연결 실패 - 유저: {} - 에러: {}", loginUser.getId(), e.getMessage());
            throw new RuntimeException("SSE 연결 실패", e);
        }

        return emitterRepository.save(loginUser.getId(), emitter);
    }

    /**
     * 사용자에게 알림을 전송합니다.
     * @param notification 보내질 알림
     */
    @Transactional
    public void sendNotification(Notification notification) {
        NotificationResponseDto notificationDto = NotificationResponseDto.toDto(notification);

        String receiverId = notification.getReceiver().getId().toString();
        String queueName = "notification.queue." + receiverId;

        // 1. Queue 이름과 Routing Key 정의
        String routingKey = receiverId;

        // 2. 메시지 발생
        try{
            // [RabbitMQ 발행] Exchange, Routing Key, 메시지 DTO
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_NAME,
                    routingKey,
                    notificationDto
            );
            log.info("RabbitMQ 발행 성공 - 유저 : {}, 타입 : {}", receiverId, notificationDto);

        } catch (AmqpException e) {
            log.error("RabbitMQ 발행 실패 - 유저 : {} - 에러 : {}", receiverId, notificationDto);
            // TODO: 실패 시 DB에 알림 저장하거나 재시도 로직 구현
        }

        // Redis Stream 에 알림 추가
        redisStreamService.addNotificationToStream(notificationDto);
    }
}

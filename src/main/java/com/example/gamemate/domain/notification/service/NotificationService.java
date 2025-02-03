package com.example.gamemate.domain.notification.service;

import com.example.gamemate.domain.notification.dto.NotificationResponseDto;
import com.example.gamemate.domain.notification.entity.Notification;
import com.example.gamemate.domain.notification.enums.NotificationType;
import com.example.gamemate.domain.notification.repository.EmitterRepository;
import com.example.gamemate.domain.notification.repository.NotificationRepository;
import com.example.gamemate.domain.user.entity.User;
import com.example.gamemate.global.constant.ErrorCode;
import com.example.gamemate.global.exception.ApiException;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmitterRepository emitterRepository;
    private final RedisStreamService redisStreamService;
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    @PostConstruct
    public void init() {
        redisStreamService.createStreamGroup();
    }

    @Transactional
    public Notification createNotification(User user, NotificationType type, String relatedUrl) {
        Notification notification = new Notification(type.getContent(), relatedUrl, type, user);
        return notificationRepository.save(notification);
    }

    public List<NotificationResponseDto> findAllNotification(User loginUser) {
        return notificationRepository.findAllByReceiverId(loginUser.getId())
                .stream()
                .map(NotificationResponseDto::toDto)
                .toList();
    }

    public SseEmitter subscribe(User loginUser) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

        try {
            // 연결 직후 더미 데이터를 보내 503 에러 방지
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("connected!"));

            // 미처리된 알림 조회 및 전송
            List<Map<String, String>> unreadNotifications =
                    redisStreamService.getUnreadNotifications(loginUser.getId());

            for (Map<String, String> notification : unreadNotifications) {
                emitter.send(SseEmitter.event()
                        .name(notification.get("type"))
                        .data(notification));
            }

        } catch (IOException e) {
            throw new RuntimeException("연결 실패!");
        }

        // emitter를 저장소에 저장 (저장 시 이벤트 핸들러도 자동 등록)
        return emitterRepository.save(loginUser.getId(), emitter);
    }

    public void sendNotification(User user, Notification notification) {
        NotificationResponseDto notificationDto = NotificationResponseDto.toDto(notification);

        // Redis 스트림에 저장
        redisStreamService.addNotificationToStream(notificationDto);

        // SSE로 전송
        SseEmitter emitter = emitterRepository.findById(user.getId());
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name(notification.getType().name())
                        .data(notificationDto));
            } catch (IOException e) {
                emitterRepository.deleteById(user.getId());
                log.error("알림 전송 실패: {}", e.getMessage());
            }
        }
    }

    @Transactional
    public void readNotification(User loginUser, Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.NOTIFICATION_NOT_FOUND));

        if (!Objects.equals(notification.getReceiver().getId(), loginUser.getId())) {
            throw new ApiException(ErrorCode.FORBIDDEN);
        } // 알림의 받는 사람과 로그인 한 유저가 다르면 예외 처리

        notification.updateIsRead(true);
    }
}

package com.example.gamemate.domain.notification.service;

import com.example.gamemate.domain.notification.dto.NotificationResponseDto;
import com.example.gamemate.domain.notification.repository.EmitterRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Duration;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisStreamService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final EmitterRepository emitterRepository;

    private static final String STREAM_KEY = "notification_stream";
    private static final String GROUP_NAME = "notification-group";
    private static final String CONSUMER_PREFIX = "consumer";
    private static final int BATCH_SIZE = 100;
    private static final Duration POLL_TIMEOUT = Duration.ofMillis(100);
    private static final int MAX_STREAM_LENGTH = 1000;

    @PostConstruct
    public void init() {
        createStreamGroup();
        initializeStreamTrimming();
    }

    public void createStreamGroup() {
        try {
            // 스트림이 없으면 생성
            if (!Boolean.TRUE.equals(redisTemplate.hasKey(STREAM_KEY))) {
                redisTemplate.opsForStream().createGroup(STREAM_KEY, ReadOffset.from("0-0"), GROUP_NAME);
                log.info("스트림과 그룹 생성 완료: {}", GROUP_NAME);
            }
            // 스트림은 있지만 그룹이 없는 경우
            else {
                try {
                    redisTemplate.opsForStream().createGroup(STREAM_KEY, GROUP_NAME);
                    log.info("기존 스트림에 그룹 생성 완료: {}", GROUP_NAME);
                } catch (Exception e) {
                    log.info("그룹이 이미 존재합니다: {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("스트림 그룹 생성 중 오류 발생: {}", e.getMessage());
        }
    }

    private void initializeStreamTrimming() {
        // 스트림 크기 제한 설정
        try {
            redisTemplate.opsForStream().trim(STREAM_KEY, MAX_STREAM_LENGTH);
        } catch (Exception e) {
            log.error("스트림 크기 초기화 중 오류 발생: {}", e.getMessage());
        }
    }

    public void addNotificationToStream(NotificationResponseDto notification) {
        try {
            Map<String, String> notificationMap = new HashMap<>();
            notificationMap.put("id", notification.getId().toString());
            notificationMap.put("content", notification.getContent());
            notificationMap.put("type", notification.getType().name());
            notificationMap.put("relatedUrl", notification.getRelatedUrl());
            notificationMap.put("receiverId", notification.getReceiverId().toString());
            notificationMap.put("timestamp", String.valueOf(System.currentTimeMillis()));

            RecordId recordId = redisTemplate.opsForStream()
                    .add(StreamRecords.newRecord()
                            .in(STREAM_KEY)
                            .ofMap(notificationMap));

            log.info("알림이 스트림에 추가됨: {}", recordId);

            // 스트림 크기 관리
            manageStream();
        } catch (Exception e) {
            log.error("알림 스트림 저장 실패: {}", e.getMessage());
            throw new RuntimeException("알림 저장 실패", e);
        }
    }

    private void manageStream() {
        try {
            // 스트림 길이 제한
            long length = redisTemplate.opsForStream().size(STREAM_KEY);
            if (length > MAX_STREAM_LENGTH) {
                redisTemplate.opsForStream().trim(STREAM_KEY, MAX_STREAM_LENGTH);
                log.info("스트림 크기 조정 완료. 현재 크기: {}", length);
            }
        } catch (Exception e) {
            log.error("스트림 관리 중 오류 발생: {}", e.getMessage());
        }
    }

    @Scheduled(fixedRate = 1000)
    public void processUnconsumedNotifications() {
        String consumerName = CONSUMER_PREFIX + "-" + UUID.randomUUID().toString();

        try {
            // 처리되지 않은 메시지 읽기
            List<MapRecord<String, Object, Object>> records =
                    redisTemplate.opsForStream().read(
                            Consumer.from(GROUP_NAME, consumerName),
                            StreamReadOptions.empty().count(BATCH_SIZE).block(POLL_TIMEOUT),
                            StreamOffset.create(STREAM_KEY, ReadOffset.lastConsumed()));

            for (MapRecord<String, Object, Object> record : records) {
                try {
                    processNotification(record);
                    // 성공적으로 처리된 메시지 승인
                    redisTemplate.opsForStream()
                            .acknowledge(GROUP_NAME, record);
                } catch (Exception e) {
                    log.error("알림 처리 실패: {}", e.getMessage());
                    // 실패한 메시지 재처리 큐에 추가
                    handleFailedNotification(record);
                }
            }
        } catch (Exception e) {
            log.error("알림 처리 중 오류 발생: {}", e.getMessage());
        }
    }

    private void processNotification(MapRecord<String, Object, Object> record) throws IOException {
        Map<Object, Object> value = record.getValue();
        Long receiverId = Long.parseLong(value.get("receiverId").toString());

        // 연결된 SSE Emitter 찾기
        SseEmitter emitter = emitterRepository.findById(receiverId);
        if (emitter != null) {
            try {
                // SSE로 알림 전송
                emitter.send(SseEmitter.event()
                        .name(value.get("type").toString())
                        .data(value));
            } catch (Exception e) {
                log.error("SSE 알림 전송 실패: {}", e.getMessage());
                emitterRepository.deleteById(receiverId);
                throw e;
            }
        }
    }

    private void handleFailedNotification(MapRecord<String, Object, Object> record) {
        // 실패한 메시지를 재처리 큐에 추가하는 로직
        try {
            String failedStreamKey = STREAM_KEY + "-failed";
            redisTemplate.opsForStream()
                    .add(StreamRecords.newRecord()
                            .in(failedStreamKey)
                            .ofMap(new HashMap<>(record.getValue())));
        } catch (Exception e) {
            log.error("실패한 알림 처리 중 오류: {}", e.getMessage());
        }
    }

    @Scheduled(fixedRate = 5000)
    public void retryFailedNotifications() {
        String failedStreamKey = STREAM_KEY + "-failed";

        try {
            List<MapRecord<String, Object, Object>> failedRecords =
                    redisTemplate.opsForStream()
                            .read(StreamOffset.fromStart(failedStreamKey));

            for (MapRecord<String, Object, Object> record : failedRecords) {
                try {
                    // 실패한 메시지 재처리
                    processNotification(record);
                    // 성공적으로 처리된 메시지 제거
                    redisTemplate.opsForStream()
                            .delete(failedStreamKey, record.getId());
                } catch (Exception e) {
                    log.error("실패한 알림 재처리 실패: {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("실패한 알림 재처리 중 오류 발생: {}", e.getMessage());
        }
    }
}
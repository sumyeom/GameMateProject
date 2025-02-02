package com.example.gamemate.domain.notification.service;

import com.example.gamemate.domain.notification.dto.NotificationResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisStreamService {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String STREAM_KEY = "notification_stream";
    private static final int MAX_STREAM_LENGTH = 1000;

    public void addNotificationToStream(NotificationResponseDto notification) {
        try {
            Map<String, String> notificationMap = new HashMap<>();
            notificationMap.put("id", notification.getId().toString());
            notificationMap.put("content", notification.getContent());
            notificationMap.put("type", notification.getType().name());
            notificationMap.put("relatedUrl", notification.getRelatedUrl());
            notificationMap.put("receiverId", notification.getReceiverId().toString());

            // 알림 추가
            redisTemplate.opsForStream().add(
                    StreamRecords.newRecord()
                            .in(STREAM_KEY)
                            .ofMap(notificationMap)
            );

            // 크기 관리
            manageStreamSize();

        } catch (Exception e) {
            log.error("스트림 저장 실패: {}", e.getMessage());
        }
    }

    public void createStreamGroup() {
        try {
            redisTemplate.opsForStream().createGroup(STREAM_KEY, "notification-group");
        } catch (Exception e) {
            log.info("스트림 그룹이 이미 존재합니다: {}", e.getMessage());
        }
    }

    private void manageStreamSize() {
        try {
            long length = redisTemplate.opsForStream().info(STREAM_KEY).streamLength();
            if (length > MAX_STREAM_LENGTH) {
                redisTemplate.opsForStream().trim(STREAM_KEY, MAX_STREAM_LENGTH, false);
            }
        } catch (Exception e) {
            log.error("스트림 크기 관리 중 오류 발생: {}", e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, String>> getUnreadNotifications(Long userId) {
        try {
            List<MapRecord<String, Object, Object>> records = redisTemplate.opsForStream()
                    .read(StreamOffset.fromStart(STREAM_KEY));

            return records.stream()
                    .map(record -> {
                        Map<Object, Object> originalMap = record.getValue();
                        Map<String, String> convertedMap = new HashMap<>();
                        originalMap.forEach((key, value) ->
                                convertedMap.put(key.toString(), value.toString())
                        );
                        return convertedMap;
                    })
                    .filter(map -> map.get("receiverId").equals(userId.toString()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("미처리 알림 조회 실패: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}
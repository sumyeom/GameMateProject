package com.example.gamemate.domain.notification.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Slf4j
public class EmitterRepository {
    // ConcurrentHashMap을 사용하여 thread-safe하게 관리
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter save(Long userId, SseEmitter emitter) {
        emitters.put(userId, emitter);
        log.info("EmitterRepository - userId: {} 연결 저장", userId);

        // 연결 종료 시 자동 제거
        emitter.onCompletion(() -> {
            log.info("EmitterRepository - userId: {} 연결 종료", userId);
            this.deleteById(userId);
        });
        emitter.onTimeout(() -> {
            log.info("EmitterRepository - userId: {} 연결 시간 초과", userId);
            this.deleteById(userId);
        });
        emitter.onError((e) -> {
            log.error("EmitterRepository - userId: {} 연결 에러: {}", userId, e.getMessage());
            this.deleteById(userId);
        });

        return emitter;
    }

    public SseEmitter findById(Long userId) {
        return emitters.get(userId);
    }

    public void deleteById(Long userId) {
        emitters.remove(userId);
    }

    public Map<Long, SseEmitter> findAll() {
        return emitters;
    }
}
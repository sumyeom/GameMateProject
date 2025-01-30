package com.example.gamemate.domain.notification.service;

import com.example.gamemate.domain.notification.dto.NotificationResponseDto;
import com.example.gamemate.domain.notification.entity.Notification;
import com.example.gamemate.domain.notification.enums.NotificationType;
import com.example.gamemate.domain.notification.repository.EmitterRepository;
import com.example.gamemate.domain.notification.repository.NotificationRepository;
import com.example.gamemate.domain.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmitterRepository emitterRepository;

    // 알림 생성
    @Transactional
    public Notification createNotification(User user, NotificationType type, String relatedUrl) {
        Notification notification = new Notification(type.getContent(), relatedUrl, type, user);
        Notification savedNotification = notificationRepository.save(notification);
        return savedNotification;
    }

    // 알림 전체 보기
    public List<NotificationResponseDto> findAllNotification(User loginUser) {

        List<Notification> notificationList = notificationRepository.findAllByReceiverId(loginUser.getId());

        return notificationList
                .stream()
                .map(NotificationResponseDto::toDto)
                .toList();
    }

    public SseEmitter subscribe(User loginUser, String lastEventId) {
        Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
        SseEmitter sseEmitter = emitterRepository.save(loginUser.getId(), new SseEmitter(DEFAULT_TIMEOUT));

        sseEmitter.onCompletion(() -> emitterRepository.deleteById(loginUser.getId()));
        sseEmitter.onTimeout(() -> emitterRepository.deleteById(loginUser.getId()));

        try {
            sseEmitter.send(
                    SseEmitter.event()
                            .id(loginUser.getId().toString())
                            .name("connect")
                            .data("connected!")
            );
        } catch (IOException e) {
            emitterRepository.deleteById(loginUser.getId());
            throw new RuntimeException("SSE 연결 오류 발생");
        }

        return sseEmitter;
    }

    @Transactional
    public void sendNotification(User user, Notification notification) {
        long startTime = System.currentTimeMillis();
        SseEmitter sseEmitter = emitterRepository.findById(user.getId());

        try {
            sseEmitter.send(
                    SseEmitter.event()
                            .id(user.getId().toString())
                            .name(notification.getType().getName())
                            .data(NotificationResponseDto.toDto(notification))
            );
        } catch (IOException e) {
            emitterRepository.deleteById(user.getId());
            throw new RuntimeException("SSE 연결 오류 발생");
        } finally {
            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;
            log.info("sendNotification 메서드 실행 시간: {}ms", elapsedTime);
        }
    }
}

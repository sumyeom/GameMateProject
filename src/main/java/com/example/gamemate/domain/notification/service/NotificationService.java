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
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final AsyncNotificationService asyncNotificationService;
    private final EmitterRepository emitterRepository;

    // 알림 생성
    @Transactional
    public void createNotification(User user, NotificationType type, String relatedUrl) {
        Notification notification = new Notification(type.getContent(), relatedUrl, type, user);
        notificationRepository.save(notification);
    }

    // 알림 전체 보기
    public List<NotificationResponseDto> findAllNotification(User loginUser) {

        List<Notification> notificationList = notificationRepository.findAllByReceiverId(loginUser.getId());

        return notificationList
                .stream()
                .map(NotificationResponseDto::toDto)
                .toList();
    }

//    // 알림 발송 (이메일)
//    @Scheduled(cron = "0 0/10 * * * *")
//    public void scheduleNotificationEmail() {
//        log.info("스케쥴링 활성화");
//
//        List<Notification> unnotifiedNotificationList = notificationRepository.findAllByIsRead(false);
//
//        if (unnotifiedNotificationList.isEmpty()) {
//            log.info("전송할 알림이 없습니다.");
//            return;
//        }
//
//        Map<User, List<Notification>> notificationMap =
//                unnotifiedNotificationList
//                        .stream()
//                        .collect(Collectors.groupingBy(Notification::getReceiver));
//
//        for (Map.Entry<User, List<Notification>> entry : notificationMap.entrySet()) {
//            User user = entry.getKey();
//            List<Notification> notifications = entry.getValue();
//
//            asyncNotificationService.sendNotificationMail(user, notifications);
//        }
//    }

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
    public void sendNotification(User user, NotificationType type, String relatedUrl) {
        SseEmitter sseEmitter = emitterRepository.findById(user.getId());
        Notification notification = new Notification(type.getContent(), relatedUrl, type, user);
        notificationRepository.save(notification);

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
        }
    }
}

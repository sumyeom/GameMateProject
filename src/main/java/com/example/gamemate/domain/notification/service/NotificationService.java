package com.example.gamemate.domain.notification.service;

import com.example.gamemate.domain.notification.dto.NotificationResponseDto;
import com.example.gamemate.domain.notification.entity.Notification;
import com.example.gamemate.domain.notification.enums.NotificationType;
import com.example.gamemate.domain.notification.repository.NotificationRepository;
import com.example.gamemate.domain.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final AsyncNotificationService asyncNotificationService;

    // 알림 생성
    @Transactional
    public void createNotification(User user, NotificationType type) {
        Notification notification = new Notification(type.getContent(), type, user);
        notificationRepository.save(notification);
    }

    // 알림 전체 보기
    public List<NotificationResponseDto> findAllNotification(User loginUser) {

        List<Notification> notificationList = notificationRepository.findAllByUserId(loginUser.getId());

        return notificationList
                .stream()
                .map(NotificationResponseDto::toDto)
                .toList();
    }

    // 알림 발송 (이메일)
    @Scheduled(cron = "0 0/10 * * * *")
    public void scheduleNotificationEmail() {
        log.info("스케쥴링 활성화");

        List<Notification> unnotifiedNotificationList = notificationRepository.findAllBySentStatus(false);

        if (unnotifiedNotificationList.isEmpty()) {
            log.info("전송할 알림이 없습니다.");
            return;
        }

        Map<User, List<Notification>> notificationMap =
                unnotifiedNotificationList
                        .stream()
                        .collect(Collectors.groupingBy(Notification::getUser));

        for (Map.Entry<User, List<Notification>> entry : notificationMap.entrySet()) {
            User user = entry.getKey();
            List<Notification> notifications = entry.getValue();

            asyncNotificationService.sendNotificationMail(user, notifications);
        }
    }
}

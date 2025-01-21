package com.example.gamemate.domain.notification.service;

import com.example.gamemate.domain.notification.entity.Notification;
import com.example.gamemate.domain.notification.repository.NotificationRepository;
import com.example.gamemate.domain.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableAsync
public class AsyncNotificationService {
    private final JavaMailSender javaMailSender;
    private final NotificationRepository notificationRepository;

    // 알림 메일 전송
    @Async
    public void sendNotificationMail(User user, List<Notification> notifications) {
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setTo(user.getEmail()); // 보낼 사람
            simpleMailMessage.setSubject("[GameMate] 새로운 알림이 있습니다."); // 제목
            simpleMailMessage.setFrom("newbiekk1126@gmail.com"); // 보내는 사람
            simpleMailMessage.setText("새로운 알림이 " + notifications.size() + "개 있습니다."); // 내용

            javaMailSender.send(simpleMailMessage);
            log.info("{}님에게 {}개의 알림 메일을 전송했습니다.", user.getEmail(), notifications.size());

            updateNotificationStatus(notifications);
        } catch (Exception e) {
            log.error("알림 메일 전송 실패: {}", user.getEmail(), e);
        }
    }

    // 알림 전송 후 notified(false -> true) 상태 변경
    @Transactional
    public void updateNotificationStatus(List<Notification> notifications) {
        notifications.forEach(notification -> notification.updateSentStatus(true));
        notificationRepository.saveAll(notifications);
    }
}

package com.example.gamemate.domain.notification.service;

import com.example.gamemate.domain.notification.dto.NotificationResponseDto;
import com.example.gamemate.domain.notification.entity.Notification;
import com.example.gamemate.domain.notification.enums.NotificationType;
import com.example.gamemate.domain.notification.repository.NotificationRepository;
import com.example.gamemate.domain.user.entity.User;
import com.example.gamemate.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
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
    private final JavaMailSender javaMailSender;

    // 알림 생성
    @Transactional
    public void createNotification(User user, NotificationType type) {
        Notification notification = new Notification(type.getContent(), type, user);
        notificationRepository.save(notification);
    }

    // 알림 전체 보기
    // todo 현재 로그인이 구현되어 있지 않아 1번 유저의 알림 목록을 불러오게 설정, 추후 로그인 구현시 로그인한 유저의 id값을 넣도록 변경
    public List<NotificationResponseDto> findAllNotification() {

        List<Notification> notificationList = notificationRepository.findAllByUserId(1L);

        return notificationList
                .stream()
                .map(NotificationResponseDto::toDto)
                .toList();
    }

    // 알림 발송 (이메일)
    @Scheduled(cron = "0 0/10 * * * *")
    public void sendNotificationMail() {

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
    }

    // 알림 전송 후 notified(false -> true) 상태 변경
    @Transactional
    public void updateNotificationStatus(List<Notification> notifications) {
        notifications.forEach(notification -> notification.updateSentStatus(true));
        notificationRepository.saveAll(notifications);
    }
}

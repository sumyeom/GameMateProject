package com.example.gamemate.domain.notification.service;

import com.example.gamemate.domain.notification.dto.NotificationResponseDto;
import com.example.gamemate.domain.notification.entity.Notification;
import com.example.gamemate.domain.notification.enums.NotificationType;
import com.example.gamemate.domain.notification.repository.NotificationRepository;
import com.example.gamemate.domain.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

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
}

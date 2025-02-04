package com.example.gamemate.domain.notification.repository;

import com.example.gamemate.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByReceiverId(Long receiverId);
    List<Notification> findAllByReceiverIdAndIsRead(Long receiverId, boolean isRead);
    Optional<Notification> findTopByReceiverIdAndIsReadOrderByCreatedAtDesc(Long receiverId, boolean isRead);
}

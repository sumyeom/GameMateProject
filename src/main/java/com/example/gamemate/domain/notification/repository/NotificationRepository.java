package com.example.gamemate.domain.notification.repository;

import com.example.gamemate.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByReceiverId(Long receiverId);
    List<Notification> findAllByIsRead(boolean isRead);

}

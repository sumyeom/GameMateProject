package com.example.gamemate.domain.notification.repository;

import com.example.gamemate.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByReceiverId(Long receiverId);

    @Query("UPDATE Notification n " +
            "SET n.isRead = true " +
            "WHERE n.receiver.id = :receiverId " +
            "AND n.isRead = false")
    @Modifying
    void updateUnreadNotificationToRead(@Param("receiverId") Long receiverId);

    Optional<Notification> findTopByReceiverIdAndIsReadOrderByCreatedAtDesc(Long receiverId, boolean isRead);
}

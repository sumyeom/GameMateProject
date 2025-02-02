package com.example.gamemate.domain.notification.dto;

import com.example.gamemate.domain.notification.entity.Notification;
import com.example.gamemate.domain.notification.enums.NotificationType;
import lombok.Getter;

@Getter
public class NotificationResponseDto {
    private Long id;
    private String content;
    private NotificationType type;
    private String relatedUrl;
    private Long receiverId;

    public NotificationResponseDto(Long id, String content, NotificationType type, String relatedUrl, Long receiverId) {
        this.id = id;
        this.content = content;
        this.type = type;
        this.relatedUrl = relatedUrl;
        this.receiverId = receiverId;
    }

    public static NotificationResponseDto toDto(Notification notification) {
        return new NotificationResponseDto(notification.getId(), notification.getContent(), notification.getType(), notification.getRelatedUrl(), notification.getReceiver().getId());
    }
}

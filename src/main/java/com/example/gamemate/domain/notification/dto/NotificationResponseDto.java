package com.example.gamemate.domain.notification.dto;

import com.example.gamemate.domain.notification.entity.Notification;
import com.example.gamemate.domain.notification.enums.NotificationType;
import lombok.Getter;

@Getter
public class NotificationResponseDto {
    private Long id;
    private String content;
    private NotificationType type;

    public NotificationResponseDto(Long id, String content, NotificationType type) {
        this.id = id;
        this.content = content;
        this.type = type;
    }

    public static NotificationResponseDto toDto(Notification notification) {
        return new NotificationResponseDto(notification.getId(), notification.getContent(), notification.getType());
    }
}

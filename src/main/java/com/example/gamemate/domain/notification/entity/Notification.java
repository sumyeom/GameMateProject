package com.example.gamemate.domain.notification.entity;

import com.example.gamemate.domain.notification.enums.NotificationType;
import com.example.gamemate.domain.user.entity.User;
import com.example.gamemate.global.common.BaseCreatedEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Notification extends BaseCreatedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String content;

    @Column
    private String relatedUrl;

    @Enumerated(EnumType.STRING)
    @Column
    private NotificationType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @Column
    private boolean isRead;

    public Notification() {
    }

    public Notification(String content, String relatedUrl, NotificationType type, User receiver) {
        this.content = content;
        this.relatedUrl = relatedUrl;
        this.type = type;
        this.receiver = receiver;
        this.isRead = false;
    }

    public void updateIsRead(boolean isRead) {
        this.isRead = isRead;
    }
}

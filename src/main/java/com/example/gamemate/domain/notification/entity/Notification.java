package com.example.gamemate.domain.notification.entity;

import com.example.gamemate.domain.notification.enums.NotificationType;
import com.example.gamemate.domain.user.entity.User;
import com.example.gamemate.global.common.BaseCreatedEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
public class Notification extends BaseCreatedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String content;

    @Enumerated(EnumType.STRING)
    @Column
    private NotificationType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column
    private boolean sentStatus;

    public Notification() {
    }

    public Notification(String content, NotificationType type, User user) {
        this.content = content;
        this.type = type;
        this.user = user;
        this.sentStatus = false;
    }

    public void updateSentStatus(boolean sentStatus) {
        this.sentStatus = sentStatus;
    }
}

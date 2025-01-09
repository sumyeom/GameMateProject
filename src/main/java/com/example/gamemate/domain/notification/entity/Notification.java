package com.example.gamemate.domain.notification.entity;

import com.example.gamemate.domain.notification.enums.NotificationType;
import com.example.gamemate.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String content;

    @Enumerated(EnumType.STRING)
    @Column
    private NotificationType type;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    public Notification() {
    }

    public Notification(String content, NotificationType type, User user) {
        this.content = content;
        this.type = type;
        this.user = user;
    }
}

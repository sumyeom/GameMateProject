package com.example.gamemate.domain.match.entity;

import com.example.gamemate.domain.match.enums.MatchStatus;
import com.example.gamemate.domain.user.entity.User;
import com.example.gamemate.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "matches")
public class Match extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private MatchStatus status;

    @Column
    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    public Match() {
    }

    public Match(String message, User sender, User receiver) {
        this.status = MatchStatus.PENDING;
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
    }

    public void updateStatus(MatchStatus status) {
        this.status = status;
    }
}

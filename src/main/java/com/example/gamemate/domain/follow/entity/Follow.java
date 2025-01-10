package com.example.gamemate.domain.follow.entity;

import com.example.gamemate.domain.user.entity.User;
import com.example.gamemate.global.common.BaseCreatedEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Follow extends BaseCreatedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followee_id", nullable = false)
    private User followee;

    public Follow() {
    }

    public Follow(User follower, User followee) {
        this.follower = follower;
        this.followee = followee;
    }
}

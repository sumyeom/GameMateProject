package com.example.gamemate.domain.follow;

import com.example.gamemate.global.BaseCreatedEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Follow extends BaseCreatedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;

    @ManyToOne
    @JoinColumn(name = "followee_id", nullable = false)
    private User followee;

    public Follow() {
    }

    public Follow(User follower, User followee) {
        this.follower = follower;
        this.followee = followee;
    }
}

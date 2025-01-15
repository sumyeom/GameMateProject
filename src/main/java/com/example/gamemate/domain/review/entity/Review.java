package com.example.gamemate.domain.review.entity;

import com.example.gamemate.domain.game.entity.Game;
import com.example.gamemate.domain.user.entity.User;
import com.example.gamemate.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "review")
public class Review extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "star", nullable = false)
    private Integer star;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;

    public Review(String content, Integer star, Game gameId, User userId) {
        this.content = content;
        this.star = star;
        this.game = gameId;
        this.user = userId;
    }

    public void updateReview(String content, Integer star) {
        this.content = content;
        this.star = star;

    }
}

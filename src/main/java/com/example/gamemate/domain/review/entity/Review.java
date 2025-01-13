package com.example.gamemate.domain.review.entity;

import com.example.gamemate.domain.game.entity.Game;
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

    //    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id")
//    private User user;
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;

    public Review(String content, Integer star, Game game, Long userId) {
        this.content = content;
        this.star = star;
        this.game = game;
        this.userId = userId;
    }

    public void updateReview(String content, Integer star){
        this.content = content;
        this.star =star;

    }
}

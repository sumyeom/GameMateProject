package com.example.gamemate.review.entity;

import com.example.gamemate.base.BaseEntity;
import com.example.gamemate.game.entity.Game;
import com.example.gamemate.user.entity.User;
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

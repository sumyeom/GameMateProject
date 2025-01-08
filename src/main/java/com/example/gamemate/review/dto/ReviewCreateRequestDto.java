package com.example.gamemate.review.dto;

import com.example.gamemate.game.entity.Game;
import com.example.gamemate.review.entity.Review;
import com.example.gamemate.user.entity.User;

import lombok.Getter;

@Getter
public class ReviewCreateRequestDto {

    private String content;
    private Integer star;
    private Long gameId;  // Game 엔티티 대신 gameId만 전달
    private Long userId;

    public ReviewCreateRequestDto(String content, Integer star, Long gameId, Long userId) {
        this.content = content;
        this.star = star;
        this.gameId = gameId;
        this.userId = userId;

    }
}

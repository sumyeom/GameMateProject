package com.example.gamemate.review.dto;

import lombok.Getter;

@Getter
public class ReviewUpdateRequestDto {

    private String content;
    private Integer star;
    private Long gameId;  // Game 엔티티 대신 gameId만 전달
    private Long userId;

    public ReviewUpdateRequestDto(String content, Integer star, Long gameId, Long userId) {
        this.content = content;
        this.star = star;
        this.gameId = gameId;
        this.userId = userId;

    }
}

package com.example.gamemate.domain.review.dto;

import com.example.gamemate.domain.review.entity.Review;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReviewFindByAllResponseDto {
    private  Long id;
    private  String content;
    private  Integer star;
    private  Long gameId;
    private  Long userId;
    private  LocalDateTime createdAt;
    private String nickName;

    public ReviewFindByAllResponseDto(Review review, String nickName) {
        this.id = review.getId();
        this.content = review.getContent();
        this.star = review.getStar();
        this.gameId = review.getGame().getId();
        this.userId = review.getUser().getId();
        this.createdAt = review.getCreatedAt();
        this.nickName = nickName;
    }
}

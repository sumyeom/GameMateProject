package com.example.gamemate.domain.like.dto.response;

import lombok.Getter;

@Getter
public class ReviewLikeCountResponseDto {
    private Long reviewId;
    private Long likeCount;

    public ReviewLikeCountResponseDto(Long reviewId, Long likeCount){
        this.reviewId = reviewId;
        this.likeCount = likeCount;
    }

}

package com.example.gamemate.domain.like.dto.response;

import com.example.gamemate.domain.like.entity.ReviewLike;
import com.example.gamemate.domain.like.enums.LikeStatus;
import lombok.Getter;

@Getter
public class ReviewLikeResponseDto {
    private Long reviewId;
    private Long userId;
    private LikeStatus status;


    public ReviewLikeResponseDto(ReviewLike reviewLike){
        this.reviewId = reviewLike.getReview().getId();
        this.status = reviewLike.getStatus();
        this.userId = reviewLike.getUser().getId();
    }

}

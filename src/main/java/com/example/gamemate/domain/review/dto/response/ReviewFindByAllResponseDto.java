package com.example.gamemate.domain.review.dto.response;

import com.example.gamemate.domain.review.entity.Review;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReviewFindByAllResponseDto {
    private Long id;
    private String content;
    private Integer star;
    private Long gameId;
    private Long userId;
    private LocalDateTime createdAt;
    private String nickName;
    private Long likeCount;

    public ReviewFindByAllResponseDto(Review review, String nickName, Long likeCount) {
        this.id = review.getId();
        this.content = review.getContent();
        this.star = review.getStar();
        this.gameId = review.getGame().getId();
        this.userId = review.getUser().getId();
        this.createdAt = review.getCreatedAt();
        this.nickName = nickName;
        this.likeCount = likeCount;
    }
}

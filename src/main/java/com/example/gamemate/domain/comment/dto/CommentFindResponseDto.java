package com.example.gamemate.domain.comment.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentFindResponseDto {
    private final Long commentId;
    private final String content;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public CommentFindResponseDto(Long commentId, String content, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.commentId = commentId;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}

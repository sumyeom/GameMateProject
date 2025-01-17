package com.example.gamemate.domain.reply.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReplyResponseDto {
    private final Long id;
    private final Long commentId;
    private Long parentReplyId;
    private final String content;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public ReplyResponseDto(Long id, Long commentId, Long parentReplyId, String content, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.commentId = commentId;
        this.parentReplyId = parentReplyId;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public ReplyResponseDto(Long id, Long commentId, String content, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.commentId = commentId;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}

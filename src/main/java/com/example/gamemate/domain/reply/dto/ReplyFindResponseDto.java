package com.example.gamemate.domain.reply.dto;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
public class ReplyFindResponseDto {
    private final Long replyId;
    private final String parentReplyName;
    private final String content;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;


    public ReplyFindResponseDto(Long replyId, String parentReplyName, String content, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.replyId = replyId;
        this.parentReplyName = parentReplyName;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}

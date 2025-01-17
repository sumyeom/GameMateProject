package com.example.gamemate.domain.comment.dto;

import com.example.gamemate.domain.reply.dto.ReplyFindResponseDto;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
public class CommentFindResponseDto {
    private final Long commentId;
    private final String content;
    private final String nickname;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final List<ReplyFindResponseDto> replies;

    public CommentFindResponseDto(Long commentId, String content, String nickname, LocalDateTime createdAt, LocalDateTime updatedAt, List<ReplyFindResponseDto> replies) {
        this.commentId = commentId;
        this.content = content;
        this.nickname = nickname;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.replies = replies;
    }
}

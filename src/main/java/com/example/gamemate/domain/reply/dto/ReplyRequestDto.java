package com.example.gamemate.domain.reply.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ReplyRequestDto {
    @NotBlank(message="댓글 내용을 입력하세요.")
    private String content;

    private Long parentReplyId;

    public ReplyRequestDto(String content, Long parentReplyId) {
        this.content = content;
        this.parentReplyId = parentReplyId;
    }

    public ReplyRequestDto() {
    }
}

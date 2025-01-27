package com.example.gamemate.domain.like.dto.response;

import lombok.Getter;

@Getter
public class BoardLikeCountResponseDto {
    private Long boardId;
    private Long likeCount;

    public BoardLikeCountResponseDto(Long boardId, Long likeCount){
        this.boardId = boardId;
        this.likeCount = likeCount;
    }

}

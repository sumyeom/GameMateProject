package com.example.gamemate.domain.like.dto.response;

import com.example.gamemate.domain.like.entity.BoardLike;
import lombok.Getter;

@Getter
public class BoardLikeResponseDto {
    private Long boardId;
    private Long userId;
    private Integer status;


    public BoardLikeResponseDto(BoardLike boardLike){
        this.boardId = boardLike.getBoard().getBoardId();
        this.status = boardLike.getStatus();
        this.userId = boardLike.getUser().getId();
    }

}

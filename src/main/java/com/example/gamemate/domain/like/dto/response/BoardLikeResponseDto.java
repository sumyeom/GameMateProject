package com.example.gamemate.domain.like.dto.response;

import com.example.gamemate.domain.like.entity.BoardLike;
import com.example.gamemate.domain.like.enums.LikeStatus;
import lombok.Getter;

@Getter
public class BoardLikeResponseDto {
    private Long boardId;
    private Long userId;
    private LikeStatus status;


    public BoardLikeResponseDto(BoardLike boardLike){
        this.boardId = boardLike.getBoard().getId();
        this.status = boardLike.getStatus();
        this.userId = boardLike.getUser().getId();
    }

}

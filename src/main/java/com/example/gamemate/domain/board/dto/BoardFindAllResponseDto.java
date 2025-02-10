package com.example.gamemate.domain.board.dto;

import com.example.gamemate.domain.board.entity.Board;
import com.example.gamemate.domain.board.enums.BoardCategory;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BoardFindAllResponseDto {
    private final Long id;
    private final BoardCategory category;
    private final String title;
    private final LocalDateTime createdAt;
    private final int views;

    public BoardFindAllResponseDto(Long id, BoardCategory category, String title, LocalDateTime createdAt, int views) {
        this.id = id;
        this.category = category;
        this.title = title;
        this.createdAt = createdAt;
        this.views = views;
    }

    public BoardFindAllResponseDto(Board board) {
        this.id = board.getId();
        this.category = board.getCategory();
        this.title = board.getTitle();
        this.createdAt = board.getCreatedAt();
        this.views = board.getViews();
    }
}

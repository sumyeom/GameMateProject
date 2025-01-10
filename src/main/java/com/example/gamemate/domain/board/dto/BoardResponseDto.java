package com.example.gamemate.domain.board.dto;

import com.example.gamemate.domain.board.enums.BoardCategory;
import lombok.Getter;

@Getter
public class BoardResponseDto {
    private final Long id;
    private final BoardCategory category;
    private final String title;
    private final String content;

    public BoardResponseDto(Long id, BoardCategory category, String title, String content) {
        this.id = id;
        this.category = category;
        this.title = title;
        this.content = content;
    }
}

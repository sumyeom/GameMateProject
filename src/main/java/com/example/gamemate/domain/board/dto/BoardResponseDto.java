package com.example.gamemate.domain.board.dto;

import com.example.gamemate.domain.board.enums.BoardCategory;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BoardResponseDto {
    private final Long id;
    private final BoardCategory category;
    private final String title;
    private final String content;
    private final String nickname;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public BoardResponseDto(Long id, BoardCategory category, String title, String content, String nickname, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.category = category;
        this.title = title;
        this.content = content;
        this.nickname = nickname;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}

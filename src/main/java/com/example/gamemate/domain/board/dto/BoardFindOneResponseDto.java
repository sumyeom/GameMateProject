package com.example.gamemate.domain.board.dto;

import com.example.gamemate.domain.board.enums.BoardCategory;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class BoardFindOneResponseDto {
    private final Long id;
    private final BoardCategory category;
    private final String title;
    private final String content;
    //private final String nickname;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    public BoardFindOneResponseDto(Long id, BoardCategory category, String title, String content, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.category = category;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }
}

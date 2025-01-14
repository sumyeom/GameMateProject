package com.example.gamemate.domain.board.dto;

import com.example.gamemate.domain.board.enums.BoardCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class BoardRequestDto {
    @NotNull(message = "카테고리를 선택하세요.")
    private final BoardCategory category;
    @NotBlank(message = "제목을 입력하세요.")
    private final String title;
    @NotBlank(message = "내용을 입력하세요.")
    private final String content;

    public BoardRequestDto(BoardCategory category, String title, String content) {
        this.category = category;
        this.title = title;
        this.content = content;
    }
}

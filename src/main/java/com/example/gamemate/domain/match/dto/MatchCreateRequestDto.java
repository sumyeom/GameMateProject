package com.example.gamemate.domain.match.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class MatchCreateRequestDto {
    @NotNull
    private Long userId;

    @NotNull
    @Size(max = 100, message = "메시지는 100자를 초과할 수 없습니다.")
    private String message;

    public MatchCreateRequestDto(Long userId, String message) {
        this.userId = userId;
        this.message = message;
    }
}

package com.example.gamemate.domain.match.dto;

import lombok.Getter;

@Getter
public class MatchCreateRequestDto {
    private Long userId;
    private String message;

    public MatchCreateRequestDto(Long userId, String message) {
        this.userId = userId;
        this.message = message;
    }
}

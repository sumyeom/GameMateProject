package com.example.gamemate.domain.match.dto;

import lombok.Getter;

@Getter
public class MatchCreateResponseDto {
    private String message;

    public MatchCreateResponseDto(String message) {
        this.message = message;
    }
}

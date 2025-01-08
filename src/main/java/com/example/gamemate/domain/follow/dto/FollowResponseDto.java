package com.example.gamemate.domain.follow.dto;

import lombok.Getter;

@Getter
public class FollowResponseDto {
    private String message;

    public FollowResponseDto(String message) {
        this.message = message;
    }
}

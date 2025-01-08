package com.example.gamemate.domain.follow.dto;

import lombok.Getter;

@Getter
public class FollowStatusResponseDto {
    private String message;

    public FollowStatusResponseDto(String message) {
        this.message = message;
    }
}

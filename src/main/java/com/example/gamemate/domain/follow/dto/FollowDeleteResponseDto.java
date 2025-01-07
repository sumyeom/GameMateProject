package com.example.gamemate.domain.follow.dto;

import lombok.Getter;

@Getter
public class FollowDeleteResponseDto {
    private String message;

    public FollowDeleteResponseDto(String message) {
        this.message = message;
    }
}

package com.example.gamemate.domain.follow.dto;

import lombok.Getter;

@Getter
public class FollowCreateResponseDto {
    private String message;

    public FollowCreateResponseDto(String message) {
        this.message = message;
    }
}

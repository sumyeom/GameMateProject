package com.example.gamemate.domain.follow.dto;

import lombok.Getter;

@Getter
public class FollowCreateRequestDto {
    private String email;

    public FollowCreateRequestDto(String email) {
        this.email = email;
    }
}

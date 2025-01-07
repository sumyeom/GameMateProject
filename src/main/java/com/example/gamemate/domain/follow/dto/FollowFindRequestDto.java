package com.example.gamemate.domain.follow.dto;

import lombok.Getter;

@Getter
public class FollowFindRequestDto {
    private String email;

    public FollowFindRequestDto(String email) {
        this.email = email;
    }
}

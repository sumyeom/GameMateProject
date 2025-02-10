package com.example.gamemate.domain.like.dto.request;

import com.example.gamemate.domain.like.enums.LikeStatus;

public class LikeRequestDto {
    private final LikeStatus status;

    public LikeRequestDto(LikeStatus status) {
        this.status = status;
    }

    public LikeStatus getStatus() {
        return status;
    }
}


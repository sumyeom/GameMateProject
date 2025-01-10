package com.example.gamemate.domain.follow.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class FollowResponseDto {
    private Long id;
    private Long followerId;
    private Long followeeId;
    private LocalDateTime createdAt;

    public FollowResponseDto(Long id, Long followerId, Long followeeId, LocalDateTime createdAt) {
        this.id = id;
        this.followerId = followerId;
        this.followeeId = followeeId;
        this.createdAt = createdAt;
    }
}

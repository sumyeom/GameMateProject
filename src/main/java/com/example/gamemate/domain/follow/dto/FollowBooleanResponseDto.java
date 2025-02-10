package com.example.gamemate.domain.follow.dto;

import lombok.Getter;

@Getter
public class FollowBooleanResponseDto {
    private boolean isFollowing;
    private Long followerId;
    private Long followeeId;

    public FollowBooleanResponseDto(boolean isFollowing, Long followerId, Long followeeId) {
        this.isFollowing = isFollowing;
        this.followerId = followerId;
        this.followeeId = followeeId;
    }
}

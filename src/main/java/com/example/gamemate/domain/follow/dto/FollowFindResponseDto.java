package com.example.gamemate.domain.follow.dto;

import com.example.gamemate.domain.user.entity.User;
import lombok.Getter;

@Getter
public class FollowFindResponseDto {
    private Long id;
    private String nickname;

    public FollowFindResponseDto(Long id, String nickname) {
        this.id = id;
        this.nickname = nickname;
    }

    public static FollowFindResponseDto toDto(User user) {
        return new FollowFindResponseDto(user.getId(), user.getNickname());
    }
}

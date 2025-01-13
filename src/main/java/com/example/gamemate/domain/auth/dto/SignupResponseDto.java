package com.example.gamemate.domain.auth.dto;

import com.example.gamemate.domain.user.entity.User;
import lombok.Getter;

@Getter
public class SignupResponseDto {

    private final Long id;
    private final String email;
    private final String name;
    private final String nickname;

    public SignupResponseDto(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.nickname = user.getNickname();
    }
}

package com.example.gamemate.domain.user.dto;

import com.example.gamemate.domain.user.entity.User;
import lombok.Getter;

@Getter
public class ProfileResponseDto {

    private Long id;
    private String email;
    private String name;
    private String nickname;
    private Boolean is_premium;

    public ProfileResponseDto(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.nickname = user.getNickname();
        this.is_premium = user.getIsPremium();
    }
}

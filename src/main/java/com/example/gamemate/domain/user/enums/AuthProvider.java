package com.example.gamemate.domain.user.enums;

import lombok.Getter;

@Getter
public enum AuthProvider {

    EMAIL("email"),
    GOOGLE("user"),
    KAKAO("admin");

    private String name;

    AuthProvider(String name) {
        this.name = name;
    }

}

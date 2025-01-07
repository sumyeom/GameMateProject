package com.example.gamemate.domain.user.enums;

import lombok.Getter;

@Getter
public enum Authority {

    USER("user"),
    ADMIN("admin");

    private String name;

    Authority(String name) {
        this.name = name;
    }
}

package com.example.gamemate.domain.user.enums;

import lombok.Getter;

@Getter
public enum Role {

    USER("user"),
    ADMIN("admin");

    private String name;

    Role(String name) {
        this.name = name;
    }
}

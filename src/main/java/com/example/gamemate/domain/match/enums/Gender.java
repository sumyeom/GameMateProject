package com.example.gamemate.domain.match.enums;

import lombok.Getter;

@Getter
public enum Gender {
    MALE("male"),
    FEMALE("female");

    private final String name;

    Gender(String name) {
        this.name = name;
    }
}

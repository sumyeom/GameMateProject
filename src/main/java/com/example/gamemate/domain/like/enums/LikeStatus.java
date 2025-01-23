package com.example.gamemate.domain.like.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum LikeStatus {
    LIKE("like"),
    DISLIKE("disLike"),
    NEUTRAL("neutral");

    private final String value;

    LikeStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static LikeStatus fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (LikeStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid LikeStatus value: " + value);
    }
}

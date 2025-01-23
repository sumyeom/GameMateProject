package com.example.gamemate.domain.like.enums;

public enum LikeStatus {
    LIKE("like"),
    DISLIKE("disLike"),
    NEUTRAL("neutral");

    private final String value;

    LikeStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static LikeStatus fromValue(String value) {
        for (LikeStatus status : values()) {
            if (status.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid LikeStatus value: " + value);
    }
}

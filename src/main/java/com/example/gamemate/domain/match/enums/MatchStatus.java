package com.example.gamemate.domain.match.enums;

public enum MatchStatus {
    ACCEPTED("accepted"),
    PENDING("pending"),
    REJECTED("rejected");

    private final String name;

    MatchStatus(String name) {
        this.name = name;
    }
}

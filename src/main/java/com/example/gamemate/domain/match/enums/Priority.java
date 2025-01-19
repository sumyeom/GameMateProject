package com.example.gamemate.domain.match.enums;

import lombok.Getter;

@Getter
public enum Priority {
    GAME_RANK("gameRank"),
    GENDER("gender"),
    LANES("lanes"),
    PLAY_TIME_RANGES("playTimeRanges"),
    PURPOSES("purposes"),
    MIC_USAGE("micUsage"),
    SKILL_LEVEL("skillLevel");

    private final String name;

    Priority(String name) {
        this.name = name;
    }
}

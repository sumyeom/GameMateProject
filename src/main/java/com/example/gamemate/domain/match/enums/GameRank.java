package com.example.gamemate.domain.match.enums;

import lombok.Getter;

@Getter
public enum GameRank {
    DONT_MIND("dont_mind", "상관없음"),
    IRON("iron", "아이언"),
    BRONZE("bronze", "브론즈"),
    SILVER("silver", "실버"),
    GOLD("gold", "골드"),
    PLATINUM("platinum", "플래티넘"),
    DIAMOND("diamond", "다이아"),
    MASTER("master", "마스터"),
    GRANDMASTER("grandmaster", "그랜드마스터"),
    CHALLENGER("challenger", "챌린저");

    private final String name;
    private final String koreanName;

    GameRank(String name, String koreanName) {
        this.name = name;
        this.koreanName = koreanName;
    }
}

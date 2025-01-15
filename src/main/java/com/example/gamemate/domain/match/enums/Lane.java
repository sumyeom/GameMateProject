package com.example.gamemate.domain.match.enums;

import lombok.Getter;

@Getter
public enum Lane {
    TOP("top", "탑"),
    JUNGLE("jungle", "정글"),
    MID("mid","정글"),
    BOTTOM_AD("bottom_ad", "원딜"),
    BOTTOM_SUPPORTER("bottom_supporter", "서포터");

    private final String name;
    private final String koreanName;

    Lane(String name, String koreanName) {
        this.name = name;
        this.koreanName = koreanName;
    }
}

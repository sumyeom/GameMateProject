package com.example.gamemate.domain.match.enums;

import lombok.Getter;

@Getter
public enum PlayTimeRange {
    ZERO_TO_SIX("zero_to_six", "0~6시"),
    SIX_TO_TWELVE("six_to_twelve", "6~12시"),
    TWELVE_TO_EIGHTEEN("twelve_to_eighteen", "12~18시"),
    EIGHTEEN_TO_TWENTY_FOUR("eighteen_to_twenty_four", "18시~24시");

    private final String name;
    private final String koreanName;

    PlayTimeRange(String name, String koreanName) {
        this.name = name;
        this.koreanName = koreanName;
    }
}

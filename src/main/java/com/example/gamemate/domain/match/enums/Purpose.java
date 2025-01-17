package com.example.gamemate.domain.match.enums;

import lombok.Getter;

@Getter
public enum Purpose {
    JUST_FOR_FUN("just_for_fun", "즐겜"),
    TRY_HARD("try_hard", "빡겜"),
    RANK_GAME("rank_game", "랭겜"),
    NORMAL_GAME("normal_game", "일반겜"),
    TEAMWORK("teamwork", "팀워크"),
    WANT_FRIEND("want_friend", "친구 구함"),
    MENTORING("mentoring", "멘토/멘티 구함"),
    DUO_PLAY("duo_play", "듀오할 사람 구함"),
    BEGINNER_FRIENDLY("beginner_friendly","뉴비 구함");

    private final String name;
    private final String koreanName;

    Purpose(String name, String koreanName) {
        this.name = name;
        this.koreanName = koreanName;
    }
}

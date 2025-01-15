package com.example.gamemate.domain.match.dto;

import com.example.gamemate.domain.match.entity.MatchUserInfo;
import com.example.gamemate.domain.match.enums.*;
import lombok.Getter;

import java.util.Set;

@Getter
public class CreateMyInfoResponseDto {
    private Long id;
    private Gender gender;
    private Set<Lane> lanes;
    private Set<Purpose> purposes;
    private GameRank gameRank;
    private Set<PlayTimeRange> playTimeRanges;
    private Integer skillLevel;
    private Boolean micUsage;
    private String message;

    public CreateMyInfoResponseDto(
            Long id, Gender gender,
            Set<Lane> lanes,
            Set<Purpose> purposes,
            GameRank gameRank,
            Set<PlayTimeRange> playTimeRanges,
            Integer skillLevel,
            Boolean micUsage,
            String message
    ) {
        this.id = id;
        this.gender = gender;
        this.lanes = lanes;
        this.purposes = purposes;
        this.gameRank = gameRank;
        this.playTimeRanges = playTimeRanges;
        this.skillLevel = skillLevel;
        this.micUsage = micUsage;
        this.message = message;
    }

    public static CreateMyInfoResponseDto toDto(MatchUserInfo matchUserInfo) {
        return new CreateMyInfoResponseDto(
                matchUserInfo.getId(),
                matchUserInfo.getGender(),
                matchUserInfo.getLanes(),
                matchUserInfo.getPurposes(),
                matchUserInfo.getGameRank(),
                matchUserInfo.getPlayTimeRanges(),
                matchUserInfo.getSkillLevel(),
                matchUserInfo.getMicUsage(),
                matchUserInfo.getMessage()
        );
    }
}

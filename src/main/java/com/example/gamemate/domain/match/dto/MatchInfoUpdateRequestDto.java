package com.example.gamemate.domain.match.dto;

import com.example.gamemate.domain.match.enums.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.Set;

@Getter
public class MatchInfoUpdateRequestDto {
    @NotNull(message = "성별은 필수 입력값입니다.")
    private Gender gender;

    @NotNull(message = "라인은 필수 입력값입니다.")
    @Size(min = 1, max = 2, message = "라인은 1-2개 선택 가능합니다.")
    private Set<Lane> lanes;

    @NotNull(message = "목적은 필수 입력값입니다.")
    @Size(min = 1, max = 3, message = "목적은 1-3개 선택 가능합니다.")
    private Set<Purpose> purposes;

    @NotNull(message = "게임 랭크는 필수 입력값입니다.")
    private GameRank gameRank;

    @NotNull(message = "플레이 시간대는 필수 입력값입니다.")
    @Size(min = 1, max = 2, message = "플레이 시간대는 1-2개 선택 가능합니다.")
    private Set<PlayTimeRange> playTimeRanges;

    @NotNull(message = "스킬 레벨은 필수 입력값입니다.")
    @Min(value = 1, message = "스킬 레벨은 1 이상이어야 합니다.")
    @Max(value = 5, message = "스킬 레벨은 5 이하여야 합니다.")
    private Integer skillLevel;

    @NotNull(message = "마이크 사용 여부는 필수 입력값입니다.")
    private Boolean micUsage;

    @Size(max = 200, message = "메시지는 200자를 초과할 수 없습니다.")
    private String message;

    public MatchInfoUpdateRequestDto(
            Gender gender,
            Set<Lane> lanes,
            Set<Purpose> purposes,
            GameRank gameRank,
            Set<PlayTimeRange> playTimeRanges,
            Integer skillLevel,
            Boolean micUsage,
            String message
    ) {
        this.gender = gender;
        this.lanes = lanes;
        this.purposes = purposes;
        this.gameRank = gameRank;
        this.playTimeRanges = playTimeRanges;
        this.skillLevel = skillLevel;
        this.micUsage = micUsage;
        this.message = message;
    }
}

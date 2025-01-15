package com.example.gamemate.domain.match.dto;

import com.example.gamemate.domain.match.enums.MatchStatus;
import lombok.Getter;

@Getter
public class MatchUpdateRequestDto {
    private MatchStatus status;

    public MatchUpdateRequestDto(MatchStatus status) {
        this.status = status;
    }
}

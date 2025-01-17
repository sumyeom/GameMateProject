package com.example.gamemate.domain.match.dto;

import com.example.gamemate.domain.match.enums.MatchStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class MatchUpdateRequestDto {
    @NotNull
    private MatchStatus status;

    public MatchUpdateRequestDto(MatchStatus status) {
        this.status = status;
    }
}

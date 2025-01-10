package com.example.gamemate.domain.match.dto;

import com.example.gamemate.domain.match.entity.Match;
import com.example.gamemate.domain.match.enums.MatchStatus;
import lombok.Getter;

@Getter
public class MatchFindResponseDto {
    private Long id;
    private MatchStatus status;
    private String nickname;
    private String message;

    public MatchFindResponseDto(Long id, MatchStatus status, String nickname, String message) {
        this.id = id;
        this.status = status;
        this.nickname = nickname;
        this.message = message;
    }

    public static MatchFindResponseDto toDto(Match match) {
        return new MatchFindResponseDto(match.getId(), match.getStatus(), match.getSender().getNickname() , match.getMessage());
    }
}

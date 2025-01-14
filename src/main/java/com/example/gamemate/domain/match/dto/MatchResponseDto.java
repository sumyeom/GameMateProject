package com.example.gamemate.domain.match.dto;

import com.example.gamemate.domain.match.entity.Match;
import com.example.gamemate.domain.match.enums.MatchStatus;
import lombok.Getter;

@Getter
public class MatchResponseDto {
    private Long id;
    private MatchStatus status;
    private String senderNickname;
    private String receiverNickname;
    private String message;

    public MatchResponseDto(Long id, MatchStatus status, String senderNickname, String receiverNickname, String message) {
        this.id = id;
        this.status = status;
        this.senderNickname = senderNickname;
        this.receiverNickname = receiverNickname;
        this.message = message;
    }

    public static MatchResponseDto toDto(Match match) {
        return new MatchResponseDto(
                match.getId(),
                match.getStatus(),
                match.getSender().getNickname(),
                match.getReceiver().getNickname(),
                match.getMessage()
        );
    }
}

package com.example.gamemate.domain.game.dto;

import lombok.Getter;

@Getter

public class GameEnrollRequestUpdateRequestDto {
    private String title;
    private String genre;
    private String platform;
    private String description;
    private Boolean isAccepted;


    public GameEnrollRequestUpdateRequestDto(String title, String genre, String platform, String description, Boolean isAccepted) {
        this.title = title;
        this.genre = genre;
        this.platform = platform;
        this.description = description;
        this.isAccepted = isAccepted;

    }
}

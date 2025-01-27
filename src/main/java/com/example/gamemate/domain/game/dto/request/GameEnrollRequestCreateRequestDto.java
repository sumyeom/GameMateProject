package com.example.gamemate.domain.game.dto.request;

import lombok.Getter;

@Getter

public class GameEnrollRequestCreateRequestDto {
    private String title;
    private String genre;
    private String platform;
    private String description;


    public GameEnrollRequestCreateRequestDto(String title, String genre, String platform, String description) {
        this.title = title;
        this.genre = genre;
        this.platform = platform;
        this.description = description;

    }
}

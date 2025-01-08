package com.example.gamemate.game.dto;

import lombok.Getter;

@Getter
public class GameCreateRequestDto {
    private String title;
    private String genre;
    private String platform;
    private String description;


    public GameCreateRequestDto(String title, String genre, String platform , String description) {
        this.title = title;
        this.genre = genre;
        this.platform = platform;
        this.description = description;
    }
}

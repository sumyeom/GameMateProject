package com.example.gamemate.domain.game.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GameUpdateRequestDto {
    private String title;
    private String genre;
    private String platform;
    private String description;


    public GameUpdateRequestDto(String title, String genre, String platform, String description) {
        this.title = title;
        this.genre = genre;
        this.platform = platform;
        this.description = description;
    }
}

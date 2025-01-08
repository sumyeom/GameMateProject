package com.example.gamemate.game.dto;

import com.example.gamemate.game.entity.Game;
import lombok.Getter;

@Getter
public class GameUpdateResponseDto {
    private Long id;
    private String title;
    private String genre;
    private String platform;
    private String description;

    public GameUpdateResponseDto(Game game) {
        // game 객체의 필드들을 이용해 DTO의 필드들을 초기화
        this.id = game.getId();
        this.title = game.getTitle();
        this.genre = game.getGenre();
        this.platform = game.getPlatform();
        this.description = game.getDescription();
    }
}

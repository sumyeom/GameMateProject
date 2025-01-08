package com.example.gamemate.game.dto;

import com.example.gamemate.game.entity.Game;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GameSearchResponseDto {
    private final Long id;
    private final String title;
    private final String genre;
    private final String platform;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    public GameSearchResponseDto(Game game) {
        // game 객체의 필드들을 이용해 DTO의 필드들을 초기화
        this.id = game.getId();
        this.title = game.getTitle();
        this.genre = game.getGenre();
        this.platform = game.getPlatform();
        this.createdAt = game.getCreatedAt();
        this.modifiedAt = game.getModifiedAt();
    }
}

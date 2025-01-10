package com.example.gamemate.domain.game.dto;

import com.example.gamemate.domain.game.entity.Game;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GameCreateResponseDto {
    private Long id;
    private String title;
    private String genre;
    private String platform;
    private String description;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;
    private final String fileName;
    private final String imageUrl;

    public GameCreateResponseDto(Game game) {
        // game 객체의 필드들을 이용해 DTO의 필드들을 초기화
        this.id = game.getId();
        this.title = game.getTitle();
        this.genre = game.getGenre();
        this.platform = game.getPlatform();
        this.description = game.getDescription();
        this.createdAt = game.getCreatedAt();
        this.modifiedAt =game.getModifiedAt();
        this.fileName = game.getImages().isEmpty() ? null :
                game.getImages().get(0).getFileName();
        this.imageUrl = game.getImages().isEmpty() ? null :
                game.getImages().get(0).getFilePath();
    }
}

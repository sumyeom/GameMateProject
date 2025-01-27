package com.example.gamemate.domain.game.dto.response;

import com.example.gamemate.domain.game.entity.Game;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@JsonPropertyOrder({"id", "title", "genre", "platform", "description", "createdAt", "fileName", "imageUrl", "modifiedAt"})
public class GameFindByIdResponseDto {
    private final Long id;
    private final String title;
    private final String genre;
    private final String platform;
    private final String description;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;
    private final String fileName;
    private final String imageUrl;
//    private final List<ReviewFindByAllResponseDto> reviews;

    public GameFindByIdResponseDto(Game game) {
        // game 객체의 필드들을 이용해 DTO의 필드들을 초기화
        this.id = game.getId();
        this.title = game.getTitle();
        this.genre = game.getGenre();
        this.platform = game.getPlatform();
        this.description = game.getDescription();
        this.createdAt = game.getCreatedAt();
        this.modifiedAt = game.getModifiedAt();
        this.fileName = game.getImages().isEmpty() ? null :
                game.getImages().get(0).getFileName();
        this.imageUrl = game.getImages().isEmpty() ? null :
                game.getImages().get(0).getFilePath();
    }
}

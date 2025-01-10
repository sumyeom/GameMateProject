package com.example.gamemate.domain.game.dto;

import com.example.gamemate.domain.game.entity.Game;
import com.example.gamemate.domain.review.dto.ReviewFindByAllResponseDto;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

@Getter
@JsonPropertyOrder({ "id", "title", "genre", "platform", "description", "createdAt", "modifiedAt", "reviews" })
public class GameFindByIdResponseDto {
    private final Long id;
    private final String title;
    private final String genre;
    private final String platform;
    private final String description;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;
    private final Page<ReviewFindByAllResponseDto> reviews;
//    private final List<ReviewFindByAllResponseDto> reviews;

    public GameFindByIdResponseDto(Game game, Page<ReviewFindByAllResponseDto> reviews) {
        // game 객체의 필드들을 이용해 DTO의 필드들을 초기화
        this.id = game.getId();
        this.title = game.getTitle();
        this.genre = game.getGenre();
        this.platform = game.getPlatform();
        this.description = game.getDescription();
        this.createdAt = game.getCreatedAt();
        this.modifiedAt = game.getModifiedAt();
        this.reviews = reviews;
//        this.reviews = game.getReviews().stream()
//                .map(ReviewFindByAllResponseDto::new)
//                .collect(Collectors.toList());
    }
}

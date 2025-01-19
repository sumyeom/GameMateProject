package com.example.gamemate.domain.game.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GameRecommendationResponseDto {
    private String title;
    private String description;
    private Double star;
    private Double matchingScore;
    private String reasonForRecommendation;
}

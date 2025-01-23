package com.example.gamemate.domain.game.dto.response;

import com.example.gamemate.domain.game.entity.GameRecommendHistory;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GameRecommendHistorysResponseDto {
    private Long userId;
    private String title;
    private String description;
    private Double star;
    private Double matchingScore;
    private String reasonForRecommendation;

    public GameRecommendHistorysResponseDto(GameRecommendHistory gameRecommendHistory) {

        this.userId = gameRecommendHistory.getUser().getId();
        this.title = gameRecommendHistory.getTitle();
        this.description = gameRecommendHistory.getDescription();
        this.star = gameRecommendHistory.getStar();
        this.matchingScore = gameRecommendHistory.getMatchingScore();
        this.reasonForRecommendation = gameRecommendHistory.getReasonForRecommendation();
    }
}
package com.example.gamemate.domain.game.dto;

import com.example.gamemate.domain.game.entity.UserGamePreference;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Getter
public class UserGamePreferenceResponseDto {

    private Long user;
    private String preferredGenres;
    private String playStyle;
    private String playTime;
    private String difficulty;
    private String platform;
    private List<GameRecommendationResponseDto> recommendations;

    public UserGamePreferenceResponseDto(UserGamePreference userGamePreference,List<GameRecommendationResponseDto> recommendations) {
        this.user = userGamePreference.getUser().getId();
        this.preferredGenres = userGamePreference.getPreferredGenres();
        this.playStyle = userGamePreference.getPlayStyle();
        this.playTime = userGamePreference.getPlayTime();
        this.difficulty = userGamePreference.getDifficulty();
        this.platform = userGamePreference.getPlatform();
        this.recommendations =recommendations;
    }

}

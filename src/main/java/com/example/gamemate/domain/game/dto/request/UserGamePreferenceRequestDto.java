package com.example.gamemate.domain.game.dto.request;

import com.example.gamemate.domain.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserGamePreferenceRequestDto {

    private User user;
    private String preferredGenres;
    private String playStyle;
    private String playTime;
    private String difficulty;
    private String platform;
    private String extraRequest;

}

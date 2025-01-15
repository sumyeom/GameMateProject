package com.example.gamemate.domain.game.controller;

import com.example.gamemate.domain.game.dto.GameRecommendHistorysResponseDto;
import com.example.gamemate.domain.game.dto.GameRecommendationResponseDto;
import com.example.gamemate.domain.game.dto.UserGamePreferenceRequestDto;
import com.example.gamemate.domain.game.dto.UserGamePreferenceResponseDto;
import com.example.gamemate.domain.game.service.GameRecommendService;
import com.example.gamemate.global.config.auth.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/games/recommendations")
@RequiredArgsConstructor
public class GameRecommendContorller {

    private final GameRecommendService gameRecommendService;

    @PostMapping
    public ResponseEntity<UserGamePreferenceResponseDto> createUserGamePreference(
            @RequestBody UserGamePreferenceRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        UserGamePreferenceResponseDto responseDto = gameRecommendService.createUserGamePreference(requestDto, customUserDetails.getUser());

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Page<GameRecommendHistorysResponseDto>> getGameRecommendHistories(
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        Page<GameRecommendHistorysResponseDto> responseDto = gameRecommendService.getGameRecommendHistories(userId, customUserDetails.getUser());
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

}

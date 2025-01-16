package com.example.gamemate.domain.game.controller;

import com.example.gamemate.domain.game.dto.GameRecommendHistorysResponseDto;
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

/**
 * 게임 추천 관련 API를 처리하는 컨트롤러 클래스입니다.
 * 사용자 게임 선호도 생성 및 게임 추천 이력 조회 기능을 제공합니다.
 */
@RestController
@RequestMapping("/games/recommendations")
@RequiredArgsConstructor
public class GameRecommendContorller {

    private final GameRecommendService gameRecommendService;

    /**
     * 사용자의 게임 선호도를 생성합니다.
     *
     * @param requestDto 사용자 게임 선호도 정보
     * @param customUserDetails 인증된 사용자 정보
     * @return 생성된 사용자 게임 선호도 정보를 포함한 ResponseEntity
     */
    @PostMapping
    public ResponseEntity<UserGamePreferenceResponseDto> createUserGamePreference(
            @RequestBody UserGamePreferenceRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {

        UserGamePreferenceResponseDto responseDto = gameRecommendService.createUserGamePreference(requestDto, customUserDetails.getUser());

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    /**
     * 특정 사용자의 게임 추천 이력을 조회합니다.
     *
     * @param userId 조회할 사용자의 ID
     * @param customUserDetails 인증된 사용자 정보
     * @return 게임 추천 이력 목록을 포함한 ResponseEntity
     */
    @GetMapping("/{userId}")
    public ResponseEntity<Page<GameRecommendHistorysResponseDto>> getGameRecommendHistories(
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {

        Page<GameRecommendHistorysResponseDto> responseDto = gameRecommendService.getGameRecommendHistories(userId, customUserDetails.getUser());
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

}

package com.example.gamemate.domain.game.controller;

import com.example.gamemate.domain.game.dto.request.GameEnrollRequestCreateRequestDto;
import com.example.gamemate.domain.game.dto.response.GameEnrollRequestResponseDto;
import com.example.gamemate.domain.game.dto.request.GameEnrollRequestUpdateRequestDto;
import com.example.gamemate.domain.game.service.GameEnrollRequestService;
import com.example.gamemate.global.config.auth.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 게임 등록 요청 관련 API를 처리하는 컨트롤러 클래스입니다.
 * 게임 등록 요청의 생성, 조회, 수정, 삭제 기능을 제공합니다.
 */
@RestController
@RequestMapping("/games/requests")
@RequiredArgsConstructor
public class GameEnrollRequestController {
    private final GameEnrollRequestService gameEnrollRequestService;

    /**
     * 새로운 게임 등록 요청을 생성합니다.
     *
     * @param requestDto 게임 등록 요청 데이터
     * @param customUserDetails 인증된 사용자 정보
     * @return 생성된 게임 등록 요청 정보를 포함한 ResponseEntity
     */
    @PostMapping
    public ResponseEntity<GameEnrollRequestResponseDto> CreateGameEnrollRequest(
            @RequestBody GameEnrollRequestCreateRequestDto requestDto,
            @Valid @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        GameEnrollRequestResponseDto responseDto = gameEnrollRequestService.createGameEnrollRequest(requestDto, customUserDetails.getUser());
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    /**
     * 모든 게임 등록 요청을 조회합니다.
     *
     * @param customUserDetails 인증된 사용자 정보
     * @return 게임 등록 요청 목록을 포함한 ResponseEntity
     */
    @GetMapping
    public ResponseEntity<Page<GameEnrollRequestResponseDto>> findAllGameEnrollRequest(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        Page<GameEnrollRequestResponseDto> gameEnrollRequestAll = gameEnrollRequestService.findAllGameEnrollRequest(customUserDetails.getUser());
        return new ResponseEntity<>(gameEnrollRequestAll, HttpStatus.OK);

    }

    /**
     * 모든 게임 등록 요청을 조회합니다.
     *
     * @param customUserDetails 인증된 사용자 정보
     * @return 게임 등록 요청 목록을 포함한 ResponseEntity
     */
    @GetMapping("/{id}")
    public ResponseEntity<GameEnrollRequestResponseDto> findGameEnrollRequestById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        GameEnrollRequestResponseDto gameEnrollRequestById = gameEnrollRequestService.findGameEnrollRequestById(id, customUserDetails.getUser());
        return new ResponseEntity<>(gameEnrollRequestById, HttpStatus.OK);
    }

    /**
     * 특정 ID의 게임 등록 요청을 수정하고, 필요시 게임 등록 기능과 연계합니다.
     *
     * @param id 수정할 게임 등록 요청의 ID
     * @param requestDto 수정할 게임 등록 요청 데이터
     * @param customUserDetails 인증된 사용자 정보
     * @return 수정 결과를 나타내는 ResponseEntity
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateGameEnroll(
            @PathVariable Long id,
            @Valid @RequestBody GameEnrollRequestUpdateRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

       gameEnrollRequestService.updateGameEnroll(id, requestDto, customUserDetails.getUser());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * 특정 ID의 게임 등록 요청을 삭제합니다.
     *
     * @param id 삭제할 게임 등록 요청의 ID
     * @param customUserDetails 인증된 사용자 정보
     * @return 삭제 결과를 나타내는 ResponseEntity
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGameEnroll(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        gameEnrollRequestService.deleteGameEnroll(id,customUserDetails.getUser());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

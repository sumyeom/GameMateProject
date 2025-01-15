package com.example.gamemate.domain.game.controller;

import com.example.gamemate.domain.game.dto.GameEnrollRequestCreateRequestDto;
import com.example.gamemate.domain.game.dto.GameEnrollRequestResponseDto;
import com.example.gamemate.domain.game.dto.GameEnrollRequestUpdateRequestDto;
import com.example.gamemate.domain.game.service.GameEnrollRequestService;
import com.example.gamemate.global.config.auth.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/games/requests")
@RequiredArgsConstructor
public class GameEnrollRequestController {
    private final GameEnrollRequestService gameEnrollRequestService;

    /**
     * 게임등록 요청
     *
     * @param requestDto
     * @return
     */
    @PostMapping
    public ResponseEntity<GameEnrollRequestResponseDto> CreateGameEnrollRequest(
            @RequestBody GameEnrollRequestCreateRequestDto requestDto,
            @Valid @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        GameEnrollRequestResponseDto responseDto = gameEnrollRequestService.createGameEnrollRequest(requestDto, customUserDetails.getUser());
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    /**
     * 게임등록 요청 전체 조회
     *
     * @return
     */
    @GetMapping
    public ResponseEntity<Page<GameEnrollRequestResponseDto>> findAllGameEnrollRequest(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        Page<GameEnrollRequestResponseDto> gameEnrollRequestAll = gameEnrollRequestService.findAllGameEnrollRequest(customUserDetails.getUser());
        return new ResponseEntity<>(gameEnrollRequestAll, HttpStatus.OK);

    }

    /**
     * 게임등록 요청 단건 조회
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<GameEnrollRequestResponseDto> findGameEnrollRequestById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        GameEnrollRequestResponseDto gameEnrollRequestById = gameEnrollRequestService.findGameEnrollRequestById(id, customUserDetails.getUser());
        return new ResponseEntity<>(gameEnrollRequestById, HttpStatus.OK);
    }

    /**
     * 게임등록 요청 수정 & 게임등록 기능 연계
     *
     * @param id
     * @param requestDto
     * @return
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateGameEnroll(
            @PathVariable Long id,
            @Valid @RequestBody GameEnrollRequestUpdateRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

       gameEnrollRequestService.updateGameEnroll(id, requestDto, customUserDetails.getUser());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGameEnroll(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        gameEnrollRequestService.deleteGameEnroll(id,customUserDetails.getUser());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

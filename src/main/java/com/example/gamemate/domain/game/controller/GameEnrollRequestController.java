package com.example.gamemate.domain.game.controller;

import com.example.gamemate.domain.game.dto.GameEnrollRequestCreateRequestDto;
import com.example.gamemate.domain.game.dto.GameEnrollRequestResponseDto;
import com.example.gamemate.domain.game.dto.GameEnrollRequestUpdateRequestDto;
import com.example.gamemate.domain.game.service.GameEnrollRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/games/requests")
public class GameEnrollRequestController {
    private final GameEnrollRequestService gameEnrollRequestService;

    @Autowired
    public GameEnrollRequestController(
            GameEnrollRequestService gameEnrollRequestService) {
        this.gameEnrollRequestService = gameEnrollRequestService;
    }

    /**
     * 게임등록 요청
     *
     * @param requestDto
     * @return
     */
    @PostMapping
    public ResponseEntity<GameEnrollRequestResponseDto> CreateGameEnrollRequest(
            @RequestBody GameEnrollRequestCreateRequestDto requestDto) {

        GameEnrollRequestResponseDto responseDto = gameEnrollRequestService.createGameEnrollRequest(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /**
     * 게임등록 요청 전체 조회
     *
     * @return
     */
    @GetMapping
    public ResponseEntity<Page<GameEnrollRequestResponseDto>> findAllGameEnrollRequest() {

        Page<GameEnrollRequestResponseDto> gameEnrollRequestAll = gameEnrollRequestService.findAllGameEnrollRequest();
        return ResponseEntity.ok(gameEnrollRequestAll);
    }

    /**
     * 게임등록 요청 단건 조회
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<GameEnrollRequestResponseDto> findGameEnrollRequestById(
            @PathVariable Long id) {

        GameEnrollRequestResponseDto gameEnrollRequestById = gameEnrollRequestService.findGameEnrollRequestById(id);
        return ResponseEntity.ok(gameEnrollRequestById);
    }

    /**
     * 게임등록 요청 수정 & 게임등록 기능 연계
     *
     * @param id
     * @param requestDto
     * @return
     */
    @PatchMapping("/{id}")
    public ResponseEntity<GameEnrollRequestResponseDto> updateGameEnroll(
            @PathVariable Long id,
            @RequestBody GameEnrollRequestUpdateRequestDto requestDto) {

       gameEnrollRequestService.updateGameEnroll(id, requestDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGame(
            @PathVariable Long id) {
        gameEnrollRequestService.deleteGame(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

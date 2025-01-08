package com.example.gamemate.game.controller;

import com.example.gamemate.game.dto.*;
import com.example.gamemate.game.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/games")
public class GameController {
    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * 게임생성
     *
     * @param gameCreateRequestDto
     * @return
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<GameCreateResponseDto> createGame(@RequestBody GameCreateRequestDto gameCreateRequestDto) {

        GameCreateResponseDto responseDto = gameService.createGame(gameCreateRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /**
     * 게임 전체 조회
     *
     * @param page
     * @param szie
     * @return
     */
    @GetMapping
    public ResponseEntity<Page<GameFindAllResponseDto>> findAllGame(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int szie) {

        Page<GameFindAllResponseDto> games = gameService.findAllGame(page, szie);
        return ResponseEntity.ok(games);
    }

    /**
     * 게임 단건 조회
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<GameFindByResponseDto> findGameById(@PathVariable Long id) {

        GameFindByResponseDto gameById = gameService.findGameById(id);
        return ResponseEntity.ok(gameById);
    }

    /**
     * 게임 정보 수정
     * @param id
     * @param requestDto
     * @return
     */
    @PatchMapping("/{id}")
    public ResponseEntity<GameUpdateResponseDto> updateGame(@PathVariable Long id, @RequestBody GameUpdateRequestDto requestDto){

        GameUpdateResponseDto responseDto = gameService.updateGame(id,requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGame(@PathVariable Long id){
        gameService.deleteGame(id);
        return ResponseEntity.ok().build();
    }
}

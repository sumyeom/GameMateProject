package com.example.gamemate.domain.game.controller;

import com.example.gamemate.domain.game.dto.*;
import com.example.gamemate.domain.game.service.GameService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/games")
@Slf4j
public class GameController {
    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {

        this.gameService = gameService;
    }

    /**
     *
     * @param gameDataString
     * @param file
     * @return
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GameCreateResponseDto> createGame(
            @RequestPart(value = "gameData") String gameDataString,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        ObjectMapper mapper = new ObjectMapper();
        GameCreateRequestDto requestDto;
        try {
            requestDto = mapper.readValue(gameDataString, GameCreateRequestDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Invalid JSON format", e);
        }

        GameCreateResponseDto responseDto = gameService.createGame(requestDto, file);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    /**
     * 게임 전체 조회
     *
     * @param page
     * @param size
     * @return
     */
    @GetMapping
    public ResponseEntity<Page<GameFindAllResponseDto>> findAllGame(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String platform,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Search parameters - keyword: {}, genre: {}, platform: {}, page: {}, size: {}",
                keyword, genre, platform, page, size);
        Page<GameFindAllResponseDto> games;
        if (keyword != null || genre != null || platform != null) {
            games = gameService.searchGame(keyword, genre, platform, page, size);
        } else {
            games = gameService.findAllGame(page, size);
        }


        return ResponseEntity.ok(games);
    }

    /**
     * 게임 단건 조회
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<GameFindByIdResponseDto> findGameById(
            @PathVariable Long id) {

        GameFindByIdResponseDto gameById = gameService.findGameById(id);
        return ResponseEntity.ok(gameById);
    }

    /**
     *
     * @param id
     * @param gameDataString
     * @param newFile
     * @return
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateGame(
            @PathVariable Long id,
            @RequestPart(value = "gameData") String gameDataString,
            @RequestPart(value = "file", required = false) MultipartFile newFile) {

        ObjectMapper mapper = new ObjectMapper();
        GameUpdateRequestDto requestDto;
        try {
            requestDto = mapper.readValue(gameDataString, GameUpdateRequestDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Invalid JSON format", e);
        }

        gameService.updateGame(id, requestDto, newFile);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGame(
            @PathVariable Long id) {

        gameService.deleteGame(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

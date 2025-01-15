package com.example.gamemate.domain.game.controller;

import com.example.gamemate.domain.game.dto.*;
import com.example.gamemate.domain.game.service.GameService;
import com.example.gamemate.global.config.auth.CustomUserDetails;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/games")
@Slf4j
@RequiredArgsConstructor
public class GameController {
    private final GameService gameService;

    /**
     * @param gameDataString
     * @param file
     * @param customUserDetails
     * @return
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GameCreateResponseDto> createGame(
            @Valid @RequestPart(value = "gameData") String gameDataString,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        ObjectMapper mapper = new ObjectMapper();
        GameCreateRequestDto requestDto;
        try {
            requestDto = mapper.readValue(gameDataString, GameCreateRequestDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Invalid JSON format", e);
        }

        GameCreateResponseDto responseDto = gameService.createGame(customUserDetails.getUser(), requestDto, file);
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

        return new ResponseEntity<>(games, HttpStatus.OK);

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
        return new ResponseEntity<>(gameById, HttpStatus.OK);

    }

    /**
     * @param id
     * @param gameDataString
     * @param newFile
     * @return
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateGame(
            @PathVariable Long id,
            @Valid @RequestPart(value = "gameData") String gameDataString,
            @RequestPart(value = "file", required = false) MultipartFile newFile,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        ObjectMapper mapper = new ObjectMapper();
        GameUpdateRequestDto requestDto;
        try {
            requestDto = mapper.readValue(gameDataString, GameUpdateRequestDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Invalid JSON format", e);
        }

        gameService.updateGame(id, requestDto, newFile, customUserDetails.getUser());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGame(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        gameService.deleteGame(id, customUserDetails.getUser());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

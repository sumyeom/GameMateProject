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

/**
 * 게임 관련 API를 처리하는 컨트롤러 클래스입니다.
 * 게임의 생성, 조회, 수정, 삭제 기능을 제공합니다.
 */
@RestController
@RequestMapping("/games")
@Slf4j
@RequiredArgsConstructor
public class GameController {
    private final GameService gameService;

    /**
     * 새로운 게임을 생성합니다.
     *
     * @param gameDataString 게임 데이터를 포함한 JSON 문자열
     * @param file 게임 관련 이미지 파일 (선택적)
     * @param customUserDetails 인증된 사용자 정보
     * @return 생성된 게임 정보를 포함한 ResponseEntity
     * @throws RuntimeException JSON 파싱 오류 시 발생
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
     * 모든 게임을 페이지네이션하여 조회하거나 검색합니다.
     *
     * @param keyword 검색 키워드 (선택적)
     * @param genre 게임 장르 (선택적)
     * @param platform 게임 플랫폼 (선택적)
     * @param page 페이지 번호 (기본값: 0)
     * @param size 페이지 크기 (기본값: 10)
     * @return 게임 목록을 포함한 ResponseEntity
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
     * 특정 ID의 게임 정보를 수정합니다.
     *
     * @param id 수정할 게임의 ID
     * @param gameDataString 수정할 게임 데이터를 포함한 JSON 문자열
     * @param newFile 새로운 게임 이미지 파일 (선택적)
     * @param customUserDetails 인증된 사용자 정보
     * @return 수정 결과를 나타내는 ResponseEntity
     * @throws RuntimeException JSON 파싱 오류 시 발생
     */
    @GetMapping("/{id}")
    public ResponseEntity<GameFindByIdResponseDto> findGameById(
            @PathVariable Long id) {

        GameFindByIdResponseDto gameById = gameService.findGameById(id);
        return new ResponseEntity<>(gameById, HttpStatus.OK);

    }

    /**
     * 특정 ID의 게임을 삭제합니다.
     *
     * @param id 삭제할 게임의 ID
     * @param customUserDetails 인증된 사용자 정보
     * @return 삭제 결과를 나타내는 ResponseEntity
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

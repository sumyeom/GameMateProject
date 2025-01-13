package com.example.gamemate.domain.game.service;

import com.example.gamemate.domain.game.dto.*;

import com.example.gamemate.domain.game.entity.Game;
import com.example.gamemate.domain.game.entity.GameImage;
import com.example.gamemate.domain.game.repository.GameImageRepository;
import com.example.gamemate.domain.game.repository.GameRepository;
import com.example.gamemate.domain.review.dto.ReviewFindByAllResponseDto;
import com.example.gamemate.domain.review.entity.Review;
import com.example.gamemate.domain.review.repository.ReviewRepository;
import com.example.gamemate.global.exception.ApiException;
import com.example.gamemate.global.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;

import static com.example.gamemate.global.constant.ErrorCode.GAME_NOT_FOUND;


@Service
@Slf4j
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;
    private final ReviewRepository reviewRepository;
    private final S3Service s3Service;
    private final GameImageRepository gameImageRepository;

    @Transactional
    public GameCreateResponseDto createGame(GameCreateRequestDto gameCreateRequestDto , MultipartFile file) {

        // 게임 엔티티 생성
        Game game = new Game(
                gameCreateRequestDto.getTitle(),
                gameCreateRequestDto.getGenre(),
                gameCreateRequestDto.getPlatform(),
                gameCreateRequestDto.getDescription()
        );

        if (file != null && !file.isEmpty()) {
            try {
                String fileUrl = s3Service.uploadFile(file);
                GameImage gameImage = new GameImage(
                        file.getOriginalFilename(),
                        file.getContentType(),
                        fileUrl,
                        game
                );
                game.addImage(gameImage);
            } catch (IOException e) {
                throw new RuntimeException("파일 업로드 중 오류가 발생했습니다.", e);
            }
        }

        Game savedGame = gameRepository.save(game);
        return new GameCreateResponseDto(savedGame);
    }

    public Page<GameFindAllResponseDto> findAllGame(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        return gameRepository.findAll(pageable).map(GameFindAllResponseDto::new);
    }

    @Transactional
    public GameFindByIdResponseDto findGameById(Long id) {

        Game game = gameRepository.findGameById(id)
                .orElseThrow(() -> new ApiException(GAME_NOT_FOUND));

        Pageable pageable = PageRequest.of(0, 5, Sort.by("createdAt").descending());
        Page<Review> reviewPage = reviewRepository.findAllByGame(game, pageable);

        // Review를 ReviewFindByAllResponseDto로 변환
        Page<ReviewFindByAllResponseDto> reviews = reviewPage.map(review ->
                new ReviewFindByAllResponseDto(review)
        );

        return new GameFindByIdResponseDto(game, reviews);
    }

    @Transactional
    public void updateGame(Long id, GameUpdateRequestDto requestDto, MultipartFile newFile) {
        Game game = gameRepository.findGameById(id)
                .orElseThrow(() -> new ApiException(GAME_NOT_FOUND));

        // 기존 파일이 있고 새 파일이 업로드된 경우
            // 1. 기존 S3 파일 삭제
//        if (!game.getImages().isEmpty()) {
//            for (GameImage image : game.getImages()) {
//                s3Service.deleteFile(image.getFilePath());
//            }
//        }

        List<GameImage> gameImages = gameImageRepository.findGameImagesByGameId(id);
        if (!gameImages.isEmpty()) {
            gameImageRepository.deleteAll(gameImages);
        }

            // 2. 새 파일 업로드
        if (newFile != null && !newFile.isEmpty()) {
            try {
                String fileUrl = s3Service.uploadFile(newFile);
                GameImage gameImage = new GameImage(
                        newFile.getOriginalFilename(),
                        newFile.getContentType(),
                        fileUrl,
                        game
                );
                game.addImage(gameImage);
            } catch (IOException e) {
                throw new RuntimeException("파일 업로드 중 오류가 발생했습니다.", e);
            }
        }

        game.updateGame(
                requestDto.getTitle(),
                requestDto.getGenre(),
                requestDto.getPlatform(),
                requestDto.getDescription()
        );
        gameRepository.save(game);
    }

    @Transactional
    public void deleteGame(Long id) {
        Game game = gameRepository.findGameById(id)
                .orElseThrow(() -> new ApiException(GAME_NOT_FOUND));

        // 게임에 연결된 모든 이미지 삭제
        if (!game.getImages().isEmpty()) {
            for (GameImage image : game.getImages()) {
                s3Service.deleteFile(image.getFilePath());
            }
        }

        gameRepository.delete(game);
    }

    public Page<GameFindAllResponseDto> searchGame(String keyword, String genre, String platform, int page, int size) {

        log.info("Searching games with parameters - keyword: {}, genre: {}, platform: {}",
                keyword, genre, platform);
        Pageable pageable = PageRequest.of(page, size);
        Page<Game> games = gameRepository.searchGames(keyword, genre, platform, pageable);
        return games.map(GameFindAllResponseDto::new);
    }

}
package com.example.gamemate.domain.game.service;

import com.example.gamemate.domain.game.dto.request.GameCreateRequestDto;
import com.example.gamemate.domain.game.dto.request.GameUpdateRequestDto;
import com.example.gamemate.domain.game.dto.response.GameCreateResponseDto;
import com.example.gamemate.domain.game.dto.response.GameFindAllResponseDto;
import com.example.gamemate.domain.game.dto.response.GameFindByIdResponseDto;
import com.example.gamemate.domain.game.entity.Game;
import com.example.gamemate.domain.game.entity.GameImage;
import com.example.gamemate.domain.game.repository.GameImageRepository;
import com.example.gamemate.domain.game.repository.GameRepository;
import com.example.gamemate.domain.review.repository.ReviewRepository;
import com.example.gamemate.domain.user.entity.User;
import com.example.gamemate.domain.user.enums.Role;
import com.example.gamemate.global.constant.ErrorCode;
import com.example.gamemate.global.exception.ApiException;
import com.example.gamemate.global.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;
    private final S3Service s3Service;

    //게임 등록(생성) (only Role.ADMIN)
    @Transactional
    public GameCreateResponseDto createGame(User loginUser, GameCreateRequestDto gameCreateRequestDto, MultipartFile file) {

        if (!loginUser.getRole().equals(Role.ADMIN)) {
            throw new ApiException(ErrorCode.FORBIDDEN);
        }

        Game game = new Game(
                gameCreateRequestDto.getTitle(),
                gameCreateRequestDto.getGenre(),
                gameCreateRequestDto.getPlatform(),
                gameCreateRequestDto.getDescription()
        );

        if (file != null && !file.isEmpty()) {
            try {

                String fileName = file.getOriginalFilename();
                String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
                List<String> allowedExtensions = Arrays.asList("jpg", "jpeg");

                if (!allowedExtensions.contains(fileExtension)) {
                    throw new ApiException(ErrorCode.INVALID_FILE_EXTENSION);
                }

                String fileUrl = s3Service.uploadFile(file);
                GameImage gameImage = new GameImage(
                        file.getOriginalFilename(),
                        file.getContentType(),
                        fileUrl,
                        game
                );

                game.addImage(gameImage);
            } catch (IOException e) {
                throw new ApiException(ErrorCode.FILE_UPLOAD_ERROR);
            }

        }

        Game savedGame = gameRepository.save(game);
        return new GameCreateResponseDto(savedGame);
    }

    //게임 다건 조회
    public Page<GameFindAllResponseDto> findAllGame(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        return gameRepository.findAll(pageable).map(GameFindAllResponseDto::new);
    }

    //게임 단건 조회
    public GameFindByIdResponseDto findGameById(Long id) {

        Game game = gameRepository.findGameById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.GAME_NOT_FOUND));

        return new GameFindByIdResponseDto(game);
    }

    //게임 정보 수정 (only Role.ADMIN)
    @Transactional
    public void updateGame(Long id, GameUpdateRequestDto requestDto, MultipartFile newFile, User loginUser) {

        if (loginUser == null || !loginUser.getRole().equals(Role.ADMIN)) {
            throw new ApiException(ErrorCode.FORBIDDEN);
        }

        Game game = gameRepository.findGameById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.GAME_NOT_FOUND));

        deleteExistingImages(game);
        uploadNewImage(game, newFile);

        game.updateGame(
                requestDto.getTitle(),
                requestDto.getGenre(),
                requestDto.getPlatform(),
                requestDto.getDescription()
        );

        gameRepository.save(game);
    }

    //게임 이미지 삭제
    private void deleteExistingImages(Game game) {
        for (GameImage image : game.getImages()) {
            try {
                s3Service.deleteFile(image.getFilePath());
            } catch (Exception e) {
                log.error("Failed to delete file: {}", image.getFilePath(), e);
            }
        }
        game.getImages().clear();
    }

    //게임 이미지 등록
    private void uploadNewImage(Game game, MultipartFile newFile) {
        if (newFile != null && !newFile.isEmpty()) {
            try {

                String fileName = newFile.getOriginalFilename();
                String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
                List<String> allowedExtensions = Arrays.asList("jpg", "jpeg");

                if (!allowedExtensions.contains(fileExtension)) {
                    throw new ApiException(ErrorCode.INVALID_FILE_EXTENSION);
                }

                String fileUrl = s3Service.uploadFile(newFile);
                GameImage gameImage = new GameImage(
                        newFile.getOriginalFilename(),
                        newFile.getContentType(),
                        fileUrl,
                        game
                );
                game.addImage(gameImage);
            } catch (IOException e) {
                throw new ApiException(ErrorCode.FILE_UPLOAD_ERROR);
            }
        }
    }

    //게임 삭제 (only Role.ADMIN)
    @Transactional
    public void deleteGame(Long id, User loginUser) {

        if (!loginUser.getRole().equals(Role.ADMIN)) {
            throw new ApiException(ErrorCode.FORBIDDEN);
        }

        Game game = gameRepository.findGameById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.GAME_NOT_FOUND));

        if (!game.getImages().isEmpty()) {
            for (GameImage image : game.getImages()) {
                s3Service.deleteFile(image.getFilePath());
            }
        }

        gameRepository.delete(game);
    }

    //게임 검색
    public Page<GameFindAllResponseDto> searchGame(String keyword, String genre, String platform, int page, int size) {

        log.info("Searching games with parameters - keyword: {}, genre: {}, platform: {}",
                keyword, genre, platform);
        Pageable pageable = PageRequest.of(page, size);
        Page<Game> games = gameRepository.searchGames(keyword, genre, platform, pageable);
        return games.map(GameFindAllResponseDto::new);
    }

}
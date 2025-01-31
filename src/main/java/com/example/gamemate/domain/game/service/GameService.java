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

    /**
     * 새로운 게임을 생성합니다. 관리자 권한이 필요합니다.
     * @param loginUser 로그인한 사용자 정보
     * @param gameCreateRequestDto 게임 생성 요청 데이터
     * @param file 게임 이미지 파일
     * @return 생성된 게임 정보
     */
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

    /**
     * 모든 게임을 페이지네이션하여 조회합니다.
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 게임 목록
     */
    public Page<GameFindAllResponseDto> findAllGame(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        return gameRepository.findAll(pageable).map(GameFindAllResponseDto::new);
    }

    /**
     * 특정 ID의 게임을 조회합니다.
     * @param id 게임 ID
     * @return 조회된 게임 정보
     * @throws ApiException 게임을 찾을 수 없는 경우
     */
    public GameFindByIdResponseDto findGameById(Long id) {

        Game game = gameRepository.findGameById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.GAME_NOT_FOUND));

        return new GameFindByIdResponseDto(game);
    }

    /**
     * 게임 정보를 수정합니다. 관리자 권한이 필요합니다.
     * @param id 수정할 게임의 ID
     * @param requestDto 수정할 게임 정보
     * @param newFile 새로운 게임 이미지 파일
     * @param loginUser 로그인한 사용자 정보
     */
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

    /**
     * 기존 게임 이미지를 삭제합니다.
     * @param game 이미지를 삭제할 게임
     */
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

    /**
     * 새로운 게임 이미지를 업로드합니다.
     * @param game 이미지를 업로드할 게임
     * @param newFile 새로운 이미지 파일
     */
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

    /**
     * 게임을 삭제합니다. 관리자 권한이 필요합니다.
     * @param id 삭제할 게임의 ID
     * @param loginUser 로그인한 사용자 정보
     */
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

    /**
     * 키워드, 장르, 플랫폼으로 게임을 검색합니다.
     * @param keyword 검색 키워드
     * @param genre 게임 장르
     * @param platform 게임 플랫폼
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 검색된 게임 목록
     */
    public Page<GameFindAllResponseDto> searchGame(String keyword, String genre, String platform, int page, int size) {

        log.info("Searching games with parameters - keyword: {}, genre: {}, platform: {}",
                keyword, genre, platform);
        Pageable pageable = PageRequest.of(page, size);
        Page<Game> games = gameRepository.searchGames(keyword, genre, platform, pageable);
        return games.map(GameFindAllResponseDto::new);
    }

}
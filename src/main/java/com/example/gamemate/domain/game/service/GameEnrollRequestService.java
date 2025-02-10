package com.example.gamemate.domain.game.service;

import com.example.gamemate.domain.game.dto.request.GameEnrollRequestCreateRequestDto;
import com.example.gamemate.domain.game.dto.response.GameEnrollRequestResponseDto;
import com.example.gamemate.domain.game.dto.request.GameEnrollRequestUpdateRequestDto;

import com.example.gamemate.domain.game.entity.GamaEnrollRequest;
import com.example.gamemate.domain.game.entity.Game;
import com.example.gamemate.domain.game.repository.GameEnrollRequestRepository;
import com.example.gamemate.domain.game.repository.GameRepository;
import com.example.gamemate.domain.user.entity.User;
import com.example.gamemate.domain.user.enums.Role;
import com.example.gamemate.global.constant.ErrorCode;
import com.example.gamemate.global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
@RequiredArgsConstructor
public class GameEnrollRequestService {
    private final GameRepository gameRepository;
    private final GameEnrollRequestRepository gameEnrollRequestRepository;

    /**
     * 새로운 게임 등록 요청을 생성합니다.
     * @param requestDto 게임 등록 요청 정보를 담은 DTO
     * @param userId 요청을 생성하는 사용자
     * @return 생성된 게임 등록 요청 정보
     */
    @Transactional
    public GameEnrollRequestResponseDto createGameEnrollRequest(GameEnrollRequestCreateRequestDto requestDto, User userId) {
        GamaEnrollRequest gameEnrollRequest = new GamaEnrollRequest(
                requestDto.getTitle(),
                requestDto.getGenre(),
                requestDto.getPlatform(),
                requestDto.getDescription(),
                userId
        );
        GamaEnrollRequest saveEnrollRequest = gameEnrollRequestRepository.save(gameEnrollRequest);
        return new GameEnrollRequestResponseDto(saveEnrollRequest);
    }

    /**
     * 모든 게임 등록 요청을 조회합니다. 관리자 권한이 필요합니다.
     * @param loginUser 현재 로그인한 사용자
     * @return 게임 등록 요청 목록 (페이지네이션 적용)
     */
    public Page<GameEnrollRequestResponseDto> findAllGameEnrollRequest(User loginUser) {

        if (!loginUser.getRole().equals(Role.ADMIN)) {
            throw new ApiException(ErrorCode.FORBIDDEN);
        }

        Pageable pageable = PageRequest.of(0, 10);

        return gameEnrollRequestRepository.findAll(pageable).map(GameEnrollRequestResponseDto::new);
    }

    /**
     * 특정 ID의 게임 등록 요청을 조회합니다. 관리자 권한이 필요합니다.
     * @param id 조회할 게임 등록 요청의 ID
     * @param loginUser 현재 로그인한 사용자
     * @return 조회된 게임 등록 요청 정보
     */
    public GameEnrollRequestResponseDto findGameEnrollRequestById(Long id, User loginUser) {

        if (!loginUser.getRole().equals(Role.ADMIN)) {
            throw new ApiException(ErrorCode.FORBIDDEN);
        }

        GamaEnrollRequest gamaEnrollRequest = gameEnrollRequestRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.GAME_NOT_FOUND));

        return new GameEnrollRequestResponseDto(gamaEnrollRequest);
    }

    /**
     * 게임 등록 요청을 수정하고, 승인 시 게임을 등록합니다. 관리자 권한이 필요합니다.
     * @param id 수정할 게임 등록 요청의 ID
     * @param requestDto 수정할 게임 등록 요청 정보를 담은 DTO
     * @param loginUser 현재 로그인한 사용자
     */
    @Transactional
    public void updateGameEnroll(Long id, GameEnrollRequestUpdateRequestDto requestDto, User loginUser) {

        if (!loginUser.getRole().equals(Role.ADMIN)) {
            throw new ApiException(ErrorCode.FORBIDDEN);
        }

        GamaEnrollRequest gamaEnrollRequest = gameEnrollRequestRepository
                .findById(id).orElseThrow(() -> new ApiException(ErrorCode.GAME_NOT_FOUND));

        gamaEnrollRequest.updateGameEnroll(
                requestDto.getTitle(),
                requestDto.getGenre(),
                requestDto.getPlatform(),
                requestDto.getDescription(),
                requestDto.getIsAccepted()
        );

        gameEnrollRequestRepository.save(gamaEnrollRequest);

        // IsAccepted = true > 게임등록
        Boolean accepted = requestDto.getIsAccepted();
        if (accepted == true) {
            Game game = new Game(
                    requestDto.getTitle(),
                    requestDto.getGenre(),
                    requestDto.getPlatform(),
                    requestDto.getDescription()
            );
            gameRepository.save(game);
        }
    }

    /**
     * 게임 등록 요청을 삭제합니다. 관리자 권한이 필요합니다.
     * @param id 삭제할 게임 등록 요청의 ID
     * @param loginUser 현재 로그인한 사용자
     */
    @Transactional
    public void deleteGameEnroll(Long id, User loginUser) {

        if (!loginUser.getRole().equals(Role.ADMIN)) {
            throw new ApiException(ErrorCode.FORBIDDEN);
        }

        GamaEnrollRequest gamaEnrollRequest = gameEnrollRequestRepository
                .findById(id).orElseThrow(() -> new ApiException(ErrorCode.GAME_NOT_FOUND));
        gameEnrollRequestRepository.delete(gamaEnrollRequest);
    }

}

package com.example.gamemate.domain.game.service;

import com.example.gamemate.domain.game.dto.GameEnrollRequestCreateRequestDto;
import com.example.gamemate.domain.game.dto.GameEnrollRequestResponseDto;
import com.example.gamemate.domain.game.dto.GameEnrollRequestUpdateRequestDto;

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

    public Page<GameEnrollRequestResponseDto> findAllGameEnrollRequest(User loginUser) {

        //관리자만 게임등록요청 조회 가능함(조회)
        if (!loginUser.getRole().equals(Role.ADMIN)){
            throw new ApiException(ErrorCode.FORBIDDEN);
        }

        Pageable pageable = PageRequest.of(0, 10);

        return gameEnrollRequestRepository.findAll(pageable).map(GameEnrollRequestResponseDto::new);
    }

    @Transactional
    public GameEnrollRequestResponseDto findGameEnrollRequestById(Long id, User loginUser) {

        //관리자만 게임등록요청 조회 가능함(조회)
        if (!loginUser.getRole().equals(Role.ADMIN)){
            throw new ApiException(ErrorCode.FORBIDDEN);
        }

        GamaEnrollRequest gamaEnrollRequest = gameEnrollRequestRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.GAME_NOT_FOUND));

        return new GameEnrollRequestResponseDto(gamaEnrollRequest);
    }

    @Transactional
    public void updateGameEnroll(Long id, GameEnrollRequestUpdateRequestDto requestDto, User loginUser) {

        //관리자만 게임등록요청 수정 가능함(수정)
        if (!loginUser.getRole().equals(Role.ADMIN)){
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

        // 만약에 관리자가 true로 바꾸면 게임등록도 함께 진행됨
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

    public void deleteGameEnroll(Long id, User loginUser) {

        //관리자만 게임등록요청 삭제 가능함(삭제)
        if (!loginUser.getRole().equals(Role.ADMIN)){
            throw new ApiException(ErrorCode.FORBIDDEN);
        }

        GamaEnrollRequest gamaEnrollRequest = gameEnrollRequestRepository
                .findById(id).orElseThrow(() -> new ApiException(ErrorCode.GAME_NOT_FOUND));
        gameEnrollRequestRepository.delete(gamaEnrollRequest);
    }

}

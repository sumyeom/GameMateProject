package com.example.gamemate.domain.game.service;

import com.example.gamemate.domain.game.dto.GameEnrollRequestCreateRequestDto;
import com.example.gamemate.domain.game.dto.GameEnrollRequestResponseDto;
import com.example.gamemate.domain.game.dto.GameEnrollRequestUpdateRequestDto;

import com.example.gamemate.domain.game.entity.GamaEnrollRequest;
import com.example.gamemate.domain.game.entity.Game;
import com.example.gamemate.domain.game.repository.GameEnrollRequestRepository;
import com.example.gamemate.domain.game.repository.GameRepository;
import com.example.gamemate.global.constant.ErrorCode;
import com.example.gamemate.global.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static com.example.gamemate.global.constant.ErrorCode.GAME_NOT_FOUND;


@Service
@Slf4j
public class GameEnrollRequestService {
    private final GameRepository gameRepository;
    private final GameEnrollRequestRepository gameEnrollRequestRepository;

    @Autowired
    public GameEnrollRequestService(GameRepository gameRepository , GameEnrollRequestRepository gameEnrollRequestRepository) {

        this.gameRepository = gameRepository;

        this.gameEnrollRequestRepository = gameEnrollRequestRepository;
    }

    public GameEnrollRequestResponseDto createGameEnrollRequest(GameEnrollRequestCreateRequestDto requestDto) {
        GamaEnrollRequest gameEnrollRequest = new GamaEnrollRequest(
                requestDto.getTitle(),
                requestDto.getGenre(),
                requestDto.getPlatform(),
                requestDto.getDescription()
        );
        GamaEnrollRequest saveEnrollRequest = gameEnrollRequestRepository.save(gameEnrollRequest);
        return new GameEnrollRequestResponseDto(saveEnrollRequest);
    }

    public Page<GameEnrollRequestResponseDto> findAllGameEnrollRequest() {

        Pageable pageable = PageRequest.of(0, 10);

        return gameEnrollRequestRepository.findAll(pageable).map(GameEnrollRequestResponseDto::new);
    }

    @Transactional
    public GameEnrollRequestResponseDto findGameEnrollRequestById(Long id) {

        GamaEnrollRequest gamaEnrollRequest = gameEnrollRequestRepository.findById(id)
                .orElseThrow(() -> new ApiException(GAME_NOT_FOUND));

        return new GameEnrollRequestResponseDto(gamaEnrollRequest);
    }

    @Transactional
    public void updateGameEnroll(Long id, GameEnrollRequestUpdateRequestDto requestDto) {
        GamaEnrollRequest gamaEnrollRequest = gameEnrollRequestRepository
                .findById(id).orElseThrow(() -> new ApiException(GAME_NOT_FOUND));

        gamaEnrollRequest.updateGameEnroll(
                requestDto.getTitle(),
                requestDto.getGenre(),
                requestDto.getPlatform(),
                requestDto.getDescription(),
                requestDto.getIsAccepted()
        );
        GamaEnrollRequest updateGameEnroll = gameEnrollRequestRepository.save(gamaEnrollRequest);

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

    public void deleteGame(Long id) {
        GamaEnrollRequest gamaEnrollRequest = gameEnrollRequestRepository
                .findById(id).orElseThrow(() -> new ApiException(GAME_NOT_FOUND));
        gameEnrollRequestRepository.delete(gamaEnrollRequest);
    }

}

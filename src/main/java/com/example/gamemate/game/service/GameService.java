package com.example.gamemate.game.service;

import com.example.gamemate.game.dto.*;
import com.example.gamemate.game.entity.GamaEnrollRequest;
import com.example.gamemate.game.entity.Game;
import com.example.gamemate.game.repository.GameEnrollRequestRepository;
import com.example.gamemate.game.repository.GameRepository;
import com.example.gamemate.review.dto.ReviewFindByAllResponseDto;
import com.example.gamemate.review.entity.Review;
import com.example.gamemate.review.repository.ReviewRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.NotFoundException;


@Service
@Slf4j
public class GameService {
    private final GameRepository gameRepository;
    private final ReviewRepository reviewRepository;
    private final GameEnrollRequestRepository gameEnrollRequestRepository;

    @Autowired
    public GameService(GameRepository gameRepository, ReviewRepository reviewRepository, GameEnrollRequestRepository gameEnrollRequestRepository) {

        this.gameRepository = gameRepository;
        this.reviewRepository = reviewRepository;
        this.gameEnrollRequestRepository = gameEnrollRequestRepository;
    }

    public GameCreateResponseDto createGame(GameCreateRequestDto gameCreateRequestDto) {

        Game game = new Game(
                gameCreateRequestDto.getTitle(),
                gameCreateRequestDto.getGenre(),
                gameCreateRequestDto.getPlatform(),
                gameCreateRequestDto.getDescription()
        );
        Game savedGame = gameRepository.save(game);
        return new GameCreateResponseDto(savedGame);

    }

    public Page<GameFindAllResponseDto> findAllGame(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        return gameRepository.findAll(pageable).map(GameFindAllResponseDto::new);
    }

    @Transactional
    public GameFindByIdResponseDto findGameById(Long id) {

        Game game = gameRepository.findGameById(id).orElseThrow(() -> new NotFoundException("게임이 존재하지 않습니다."));

        Pageable pageable = PageRequest.of(0, 5, Sort.by("createdAt").descending());
        Page<Review> reviewPage = reviewRepository.findAllByGame(game, pageable);

        // Review를 ReviewFindByAllResponseDto로 변환
        Page<ReviewFindByAllResponseDto> reviews = reviewPage.map(review ->
                new ReviewFindByAllResponseDto(review)
        );

        return new GameFindByIdResponseDto(game, reviews);
    }

    @Transactional
    public GameUpdateResponseDto updateGame(Long id, GameUpdateRequestDto requestDto) {
        Game game = gameRepository.findGameById(id).orElseThrow(() -> new NotFoundException("게임이 존해 하지 않습니다."));

        game.updateGame(
                requestDto.getTitle(),
                requestDto.getGenre(),
                requestDto.getPlatform(),
                requestDto.getDescription()
        );
        Game updateGame = gameRepository.save(game);
        return new GameUpdateResponseDto(updateGame);
    }

    public void deleteGame(Long id) {
        Game game = gameRepository.findGameById(id).orElseThrow(() -> new NotFoundException("게임을 찾을 없습니다."));
        gameRepository.delete(game);
    }

    public Page<GameSearchResponseDto> searchGame(String keyword, String genre, String platform, int page, int size) {

        log.info("Searching games with parameters - keyword: {}, genre: {}, platform: {}",
                keyword, genre, platform);
        Pageable pageable = PageRequest.of(page, size);
        Page<Game> games = gameRepository.searchGames(keyword, genre, platform, pageable);
        return games.map(GameSearchResponseDto::new);
    }

}
package com.example.gamemate.game.service;

import com.example.gamemate.game.dto.*;
import com.example.gamemate.game.entity.Game;
import com.example.gamemate.game.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.NotFoundException;


@Service
public class GameService {
    private final GameRepository gameRepository;

    @Autowired
    public GameService(GameRepository gameRepository) {

        this.gameRepository = gameRepository;
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

    public Page<GameFindAllResponseDto> findAllGame(int page, int size){

        Pageable pageable = PageRequest.of(page,size);
        return gameRepository.findAll(pageable).map(GameFindAllResponseDto::new);
    }

    @Transactional
    public GameFindByResponseDto findGameById(Long id){
        Game game = gameRepository.findGameById(id).orElseThrow(()->new NotFoundException("게임이 존재하지 않습니다."));
        return new GameFindByResponseDto(game);
    }

    @Transactional
    public GameUpdateResponseDto updateGame(Long id, GameUpdateRequestDto requestDto){
        Game game = gameRepository.findGameById(id).orElseThrow(()-> new NotFoundException("게임이 존해 하지 않습니다."));

        game.updateGame(
                requestDto.getTitle(),
                requestDto.getGenre(),
                requestDto.getPlatform(),
                requestDto.getDescription()
        );
        Game updateGame = gameRepository.save(game);
        return new GameUpdateResponseDto(updateGame);
    }

    public void deleteGame(Long id){
        Game game = gameRepository.findGameById(id).orElseThrow(()-> new NotFoundException("게임을 찾을 없습니다."));
        gameRepository.delete(game);
    }
}

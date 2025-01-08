package com.example.gamemate.game.repository;

import com.example.gamemate.game.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Long> {
    // 게임 이름으로 게임 찾기
    List<Game> findGameByTitle(String title);

    // 플랫폼별 게임 찾기
    List<Game> findGamesByPlatform(String platform);

    // 장르로 게임 찾기
    List<Game> findGameByGenre(String genre);

    // 아이디로 게임 찾기
    Optional<Game>findGameById(Long id);
}

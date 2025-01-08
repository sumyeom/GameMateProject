package com.example.gamemate.game.repository;

import com.example.gamemate.game.entity.Game;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Long> {

    // 아이디로 게임 찾기
    Optional<Game> findGameById(Long id);

    // 게임 검색
    @Query("SELECT g FROM Game g WHERE " +
            "(:keyword IS NULL OR " +
            "LOWER(g.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(g.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:genre IS NULL OR :genre = '' OR LOWER(g.genre) LIKE LOWER(CONCAT('%', :genre, '%'))) " +
            "AND (:platform IS NULL OR :platform = '' OR LOWER(g.platform) LIKE LOWER(CONCAT('%', :platform, '%')))")
    Page<Game> searchGames(
            @Param("keyword") String keyword,
            @Param("genre") String genre,
            @Param("platform") String platform,
            Pageable pageable
    );
}

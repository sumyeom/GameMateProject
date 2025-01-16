package com.example.gamemate.domain.game.repository;

import com.example.gamemate.domain.game.dto.GameRecommendHistorysResponseDto;
import com.example.gamemate.domain.game.entity.GameRecommendHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameRecommendHistoryRepository extends JpaRepository<GameRecommendHistory, Long> {
    Page<GameRecommendHistory> findByUserId(Long userId, Pageable pageable);
}

package com.example.gamemate.domain.game.repository;

import com.example.gamemate.domain.game.entity.GameImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameImageRepository extends JpaRepository<GameImage, Long> {
    // 게임별 이미지 찾기
    List<GameImage> findGameImagesByGameId(Long gameId);
}

package com.example.gamemate.domain.review.repository;

import com.example.gamemate.domain.game.entity.Game;
import com.example.gamemate.domain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findAllByGame(Game game, Pageable pageable);

    boolean existsByUserIdAndGameId(Long userId, Long gameId);

}

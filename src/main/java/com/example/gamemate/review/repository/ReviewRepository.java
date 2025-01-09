package com.example.gamemate.review.repository;

import com.example.gamemate.game.entity.Game;
import com.example.gamemate.like.entity.ReviewLike;
import com.example.gamemate.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findAllByGame(Game game, Pageable pageable);
}

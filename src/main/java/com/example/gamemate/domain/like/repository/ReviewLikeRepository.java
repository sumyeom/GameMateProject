package com.example.gamemate.domain.like.repository;

import com.example.gamemate.domain.like.entity.ReviewLike;
import com.example.gamemate.domain.like.enums.LikeStatus;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {

    Optional<ReviewLike> findByReviewIdAndUserId(Long reviewId, Long userId);

    Long countByReviewIdAndStatus(Long reviewId, LikeStatus status);

}

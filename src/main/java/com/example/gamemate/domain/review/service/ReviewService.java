package com.example.gamemate.domain.review.service;

import com.example.gamemate.domain.game.entity.Game;
import com.example.gamemate.domain.game.repository.GameRepository;
import com.example.gamemate.domain.review.dto.ReviewCreateRequestDto;
import com.example.gamemate.domain.review.dto.ReviewCreateResponseDto;
import com.example.gamemate.domain.review.dto.ReviewUpdateRequestDto;
import com.example.gamemate.domain.review.dto.ReviewUpdateResponseDto;
import com.example.gamemate.domain.review.entity.Review;
import com.example.gamemate.domain.review.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import javax.ws.rs.NotFoundException;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final GameRepository gameRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         GameRepository gameRepository) {
        this.reviewRepository = reviewRepository;
        this.gameRepository = gameRepository;
    }

    public ReviewCreateResponseDto createReview(Long gameId, ReviewCreateRequestDto requestDto) {

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new NotFoundException("Game not found"));

        Review review = new Review(
                requestDto.getContent(),
                requestDto.getStar(),
                game,
                requestDto.getUserId()
        );
        Review saveReview = reviewRepository.save(review);
        return new ReviewCreateResponseDto(saveReview);
    }

    public ReviewUpdateResponseDto updateReview(Long gameId, Long id, ReviewUpdateRequestDto requestDto) {

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("리뷰가 존재하지 않습니다."));

        review.updateReview(
                requestDto.getContent(),
                requestDto.getStar()
        );

        Review updateReview = reviewRepository.save(review);
        return new ReviewUpdateResponseDto(updateReview);
    }

    public void deleteReview(Long id) {

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("리뷰가 존재하지 않습니다."));

        reviewRepository.delete(review);
    }



}

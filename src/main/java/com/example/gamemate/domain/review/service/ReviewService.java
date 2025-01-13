package com.example.gamemate.domain.review.service;

import com.example.gamemate.domain.game.entity.Game;
import com.example.gamemate.domain.game.repository.GameRepository;
import com.example.gamemate.domain.review.dto.ReviewCreateRequestDto;
import com.example.gamemate.domain.review.dto.ReviewCreateResponseDto;
import com.example.gamemate.domain.review.dto.ReviewUpdateRequestDto;
import com.example.gamemate.domain.review.dto.ReviewUpdateResponseDto;
import com.example.gamemate.domain.review.entity.Review;
import com.example.gamemate.domain.review.repository.ReviewRepository;
import com.example.gamemate.domain.user.entity.User;
import com.example.gamemate.domain.user.repository.UserRepository;
import com.example.gamemate.global.constant.ErrorCode;
import com.example.gamemate.global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.gamemate.global.constant.ErrorCode.*;


@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final GameRepository gameRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReviewCreateResponseDto createReview(String email, Long gameId, ReviewCreateRequestDto requestDto) {

        // 이메일로 유저 조회
        User user = userRepository.findByEmail(email).
                orElseThrow(() -> new ApiException(USER_NOT_FOUND));
        Long userId = user.getId();

        // 사용자가 이미 해당 게임에 대한 리뷰를 작성했는지 확인
        boolean hasReview = reviewRepository.existsByUserIdAndGameId(userId, gameId);
        if (hasReview) {
            throw new ApiException(REVIEW_ALREADY_EXISTS);
        }

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ApiException(REVIEW_NOT_FOUND));

        Review review = new Review(
                requestDto.getContent(),
                requestDto.getStar(),
                game,
                userId
        );
        Review saveReview = reviewRepository.save(review);
        return new ReviewCreateResponseDto(saveReview);
    }

    public void updateReview(String email, Long gameId, Long id, ReviewUpdateRequestDto requestDto) {

        // 이메일로 유저 조회
        User user = userRepository.findByEmail(email).
                orElseThrow(() -> new ApiException(USER_NOT_FOUND));
        Long userId = user.getId();

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ApiException(REVIEW_NOT_FOUND));

        // 리뷰 작성자와 현재 사용자가 같은지 확인
        if (!review.getUserId().equals(userId)) {
            throw new ApiException(FORBIDDEN);
        }

        review.updateReview(
                requestDto.getContent(),
                requestDto.getStar()
        );

        reviewRepository.save(review);
    }

    public void deleteReview(User loginUser, Long id) {

        Long userId = loginUser.getId();

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ApiException(REVIEW_NOT_FOUND));

        // 리뷰 작성자와 현재 사용자가 같은지 확인
        if (!review.getUserId().equals(userId)) {
            throw new ApiException(FORBIDDEN);
        }

        reviewRepository.delete(review);
    }



}

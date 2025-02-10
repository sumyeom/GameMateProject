package com.example.gamemate.domain.review.service;

import com.example.gamemate.domain.game.entity.Game;
import com.example.gamemate.domain.game.repository.GameRepository;
import com.example.gamemate.domain.like.enums.LikeStatus;
import com.example.gamemate.domain.like.repository.ReviewLikeRepository;
import com.example.gamemate.domain.review.dto.request.ReviewCreateRequestDto;
import com.example.gamemate.domain.review.dto.response.ReviewCreateResponseDto;
import com.example.gamemate.domain.review.dto.response.ReviewFindByAllResponseDto;
import com.example.gamemate.domain.review.dto.request.ReviewUpdateRequestDto;
import com.example.gamemate.domain.review.entity.Review;
import com.example.gamemate.domain.review.repository.ReviewRepository;
import com.example.gamemate.domain.user.entity.User;
import com.example.gamemate.domain.user.enums.Role;
import com.example.gamemate.domain.user.repository.UserRepository;
import com.example.gamemate.global.constant.ErrorCode;
import com.example.gamemate.global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final ReviewLikeRepository reviewLikeRepository;

    //리뷰 생성
    @Transactional
    public ReviewCreateResponseDto createReview(User loginUser, Long gameId, ReviewCreateRequestDto requestDto) {

        Long userId = loginUser.getId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        boolean hasReview = reviewRepository.existsByUserIdAndGameId(userId, gameId);
        if (hasReview) {
            throw new ApiException(ErrorCode.REVIEW_ALREADY_EXISTS);
        }

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ApiException(ErrorCode.GAME_NOT_FOUND));

        Review review = new Review(
                requestDto.getContent(),
                requestDto.getStar(),
                game,
                user
        );

        Review saveReview = reviewRepository.save(review);
        return new ReviewCreateResponseDto(saveReview);
    }

    //리뷰 수정
    @Transactional
    public void updateReview(User loginUser, Long gameId, Long id, ReviewUpdateRequestDto requestDto) {

        Long userId = loginUser.getId();

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.REVIEW_NOT_FOUND));

        if (!review.getUser().getId().equals(userId)) {
            throw new ApiException(ErrorCode.FORBIDDEN);
        }

        review.updateReview(
                requestDto.getContent(),
                requestDto.getStar()
        );

        reviewRepository.save(review);
    }

    //리뷰 삭제
    @Transactional
    public void deleteReview(User loginUser, Long id) {

        Long userId = loginUser.getId();

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.REVIEW_NOT_FOUND));

        if (userId.equals(review.getUser().getId()) || loginUser.getRole() == Role.ADMIN) {
            reviewRepository.delete(review);
        } else {
            throw new ApiException(ErrorCode.FORBIDDEN);
        }

    }

    //리뷰 조회(게임별 다건 조회)
    public Page<ReviewFindByAllResponseDto> ReviewFindAllByGameId(Long gameId, User loginUser) {

        Game game = gameRepository.findGameById(gameId)
                .orElseThrow(() -> new ApiException(ErrorCode.GAME_NOT_FOUND));

        Pageable pageable = PageRequest.of(0, 5, Sort.by("createdAt").descending());
        Page<Review> reviewPage = reviewRepository.findAllByGame(game, pageable);

        return reviewPage.map(review -> {
            Long likeCount = reviewLikeRepository.countByReviewIdAndStatus(review.getId(), LikeStatus.LIKE);
            return new ReviewFindByAllResponseDto(review, loginUser.getNickname(), likeCount);
        });
    }


}

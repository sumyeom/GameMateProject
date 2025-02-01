package com.example.gamemate.domain.like.service;

import com.example.gamemate.domain.board.repository.BoardRepository;
import com.example.gamemate.domain.like.dto.response.BoardLikeResponseDto;
import com.example.gamemate.domain.like.dto.response.ReviewLikeResponseDto;
import com.example.gamemate.domain.like.entity.BoardLike;
import com.example.gamemate.domain.like.entity.ReviewLike;
import com.example.gamemate.domain.like.enums.LikeStatus;
import com.example.gamemate.domain.like.repository.BoardLikeRepository;
import com.example.gamemate.domain.like.repository.ReviewLikeRepository;
import com.example.gamemate.domain.notification.enums.NotificationType;
import com.example.gamemate.domain.notification.service.NotificationService;
import com.example.gamemate.domain.review.repository.ReviewRepository;
import com.example.gamemate.domain.user.entity.User;
import com.example.gamemate.domain.user.repository.UserRepository;
import com.example.gamemate.global.constant.ErrorCode;
import com.example.gamemate.global.eventListener.event.BoardLikeCreatedEvent;
import com.example.gamemate.global.eventListener.event.ReviewLikeCreatedEvent;
import com.example.gamemate.global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final ReviewLikeRepository reviewLikeRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final BoardLikeRepository boardLikeRepository;
    private final BoardRepository boardRepository;
    private final ApplicationEventPublisher publisher;

    //리뷰 좋아요 생성 취소 수정
    @Transactional
    public ReviewLikeResponseDto reviewLikeUp(Long reviewId, LikeStatus status, User loginUser) {

        ReviewLike reviewLike = reviewLikeRepository.findByReviewIdAndUserId(reviewId, loginUser.getId()).
                orElse(new ReviewLike(
                        status,
                        userRepository.findById(loginUser.getId())
                                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND)),
                        reviewRepository.findById(reviewId)
                                .orElseThrow(() -> new ApiException(ErrorCode.REVIEW_NOT_FOUND))
                ));

        if (reviewLike.getId() == null) {
            reviewLikeRepository.save(reviewLike);
            publisher.publishEvent(new ReviewLikeCreatedEvent(this, reviewLike));
        } else {
            reviewLike.changeStatus(status);
        }

        return new ReviewLikeResponseDto(reviewLike);
    }

    //게시물 좋아요 생성 취소 수정
    @Transactional
    public BoardLikeResponseDto boardLikeUp(Long boardId, LikeStatus status, User loginUser) {

        BoardLike boardLike = boardLikeRepository.findByBoardIdAndUserId(boardId, loginUser.getId()).
                orElse(new BoardLike(
                        status,
                        userRepository.findById(loginUser.getId())
                                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND)),
                        boardRepository.findById(boardId)
                                .orElseThrow(() -> new ApiException(ErrorCode.BOARD_NOT_FOUND))
                ));

        if (boardLike.getId() == null) {
            boardLikeRepository.save(boardLike);
            publisher.publishEvent(new BoardLikeCreatedEvent(this, boardLike));
        } else {
            boardLike.changeStatus(status);
        }

        return new BoardLikeResponseDto(boardLike);
    }

    public Long getBoardLikeCount(Long boardId) {

        boardRepository.findById(boardId)
                .orElseThrow(() -> new ApiException(ErrorCode.BOARD_NOT_FOUND));
        return boardLikeRepository.countByBoardIdAndStatus(boardId, LikeStatus.LIKE);
    }

    public Long getReivewLikeCount(Long reviewId) {

        reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ApiException(ErrorCode.REVIEW_NOT_FOUND));

        return reviewLikeRepository.countByReviewIdAndStatus(reviewId, LikeStatus.LIKE);
    }
}

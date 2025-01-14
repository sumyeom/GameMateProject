package com.example.gamemate.domain.like.service;

import com.example.gamemate.domain.board.repository.BoardRepository;
import com.example.gamemate.domain.like.entity.BoardLike;
import com.example.gamemate.domain.like.entity.ReviewLike;
import com.example.gamemate.domain.like.repository.BoardLikeRepository;
import com.example.gamemate.domain.like.repository.ReviewLikeRepository;
import com.example.gamemate.domain.review.repository.ReviewRepository;
import com.example.gamemate.domain.user.entity.User;
import com.example.gamemate.domain.user.repository.UserRepository;
import com.example.gamemate.global.constant.ErrorCode;
import com.example.gamemate.global.exception.ApiException;
import lombok.RequiredArgsConstructor;
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

    @Transactional
    public void reviewLikeUp(Long reviewId, Integer status, User loginUser) {

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
        } else {
            reviewLike.changeStatus(status);
        }
    }

    @Transactional
    public void boardLikeUp(Long boardId, Integer status, User loginUser) {

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
        } else {
            boardLike.changeStatus(status);
        }
    }

    public Long getBoardLikeCount(Long boardId) {
        return boardLikeRepository.countByBoardBoardIdAndStatus(boardId, 1);
    }
    public Long getReivewLikeCount(Long reviewId) {
        return reviewLikeRepository.countByReviewIdAndStatus(reviewId, 1);
    }
}

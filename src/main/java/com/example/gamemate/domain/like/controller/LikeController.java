package com.example.gamemate.domain.like.controller;

import com.example.gamemate.domain.like.dto.response.BoardLikeCountResponseDto;
import com.example.gamemate.domain.like.dto.response.BoardLikeResponseDto;
import com.example.gamemate.domain.like.dto.response.ReviewLikeCountResponseDto;
import com.example.gamemate.domain.like.dto.response.ReviewLikeResponseDto;
import com.example.gamemate.domain.like.service.LikeService;
import com.example.gamemate.global.config.auth.CustomUserDetails;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 좋아요 기능을 처리하는 컨트롤러 클래스입니다.
 * 리뷰와 게시판에 대한 좋아요 기능을 제공합니다.
 */
@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    /**
     * 리뷰에 대한 좋아요를 처리합니다.
     *
     * @param reviewId 좋아요를 누를 리뷰의 ID
     * @param status 좋아요 상태 (1: 좋아요, 0: 좋아요 취소, -1:싫어요)
     * @param customUserDetails 현재 인증된 사용자 정보
     * @return 좋아요 처리 결과를 담은 ResponseEntity
     */
    @PostMapping("/reviews/{reviewId}")
    public ResponseEntity<ReviewLikeResponseDto> reviewLikeUp(
            @PathVariable Long reviewId,
            @RequestBody Integer status,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        ReviewLikeResponseDto responseDto = likeService.reviewLikeUp(reviewId, status, customUserDetails.getUser());
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    /**
     * 게시글에 대한 좋아요를 처리합니다.
     *
     * @param boardId 좋아요를 누를 게시글의 ID
     * @param status 좋아요 상태 (1: 좋아요, 0: 좋아요 취소, -1:싫어요)
     * @param customUserDetails 현재 인증된 사용자 정보
     * @return 좋아요 처리 결과를 담은 ResponseEntity
     */
    @PostMapping("/boards/{boardId}")
    public ResponseEntity<BoardLikeResponseDto> boardLikeUp(
            @PathVariable Long boardId,
            @RequestBody Integer status,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        BoardLikeResponseDto responseDto = likeService.boardLikeUp(boardId, status, customUserDetails.getUser());
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    /**
     * 특정 리뷰의 좋아요 수를 조회합니다.
     *
     * @param reviewId 조회할 리뷰의 ID
     * @return 리뷰의 좋아요 수를 담은 ResponseEntity
     */
    @GetMapping("/reviews/{reviewId}")
    public ResponseEntity<ReviewLikeCountResponseDto> reviewLikeCount(
            @PathVariable Long reviewId) {

        Long likeCount = likeService.getReivewLikeCount(reviewId);
        ReviewLikeCountResponseDto responseDto = new ReviewLikeCountResponseDto(reviewId, likeCount);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    /**
     * 특정 게시글의 좋아요 수를 조회합니다.
     *
     * @param boardId 조회할 게시글의 ID
     * @return 게시글의 좋아요 수를 담은 ResponseEntity
     */
    @GetMapping("/boards/{boardId}")
    public ResponseEntity<BoardLikeCountResponseDto> boardLikeCount(
            @PathVariable Long boardId) {

        Long likeCount = likeService.getBoardLikeCount(boardId);
        BoardLikeCountResponseDto responseDto = new BoardLikeCountResponseDto(boardId, likeCount);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}

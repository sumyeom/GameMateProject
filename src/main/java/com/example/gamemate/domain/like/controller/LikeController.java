package com.example.gamemate.domain.like.controller;

import com.example.gamemate.domain.like.dto.BoardLikeCountResponseDto;
import com.example.gamemate.domain.like.dto.ReviewLikeCountResponseDto;
import com.example.gamemate.domain.like.service.LikeService;
import com.example.gamemate.global.config.auth.CustomUserDetails;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/reviews/{reviewId}")
    public ResponseEntity<Void> reviewLikeUp(
            @PathVariable Long reviewId,
            @RequestBody Integer status,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        likeService.reviewLikeUp(reviewId, status, customUserDetails.getUser());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/boards/{boardId}")
    public ResponseEntity<Void> boardLikeUp(
            @PathVariable Long boardId,
            @RequestBody Integer status,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        likeService.boardLikeUp(boardId, status, customUserDetails.getUser());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/reviews/{reviewId}")
    public ResponseEntity<ReviewLikeCountResponseDto> reviewLikeCount(
            @PathVariable Long reviewId){

        Long likeCount = likeService.getReivewLikeCount(reviewId);
        ReviewLikeCountResponseDto responseDto = new ReviewLikeCountResponseDto(reviewId, likeCount);
        return new  ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/boards/{boardId}")
    public ResponseEntity<BoardLikeCountResponseDto> boardLikeCount(
            @PathVariable Long boardId){

        Long likeCount = likeService.getBoardLikeCount(boardId);
        BoardLikeCountResponseDto responseDto = new BoardLikeCountResponseDto(boardId, likeCount);
        return new  ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}

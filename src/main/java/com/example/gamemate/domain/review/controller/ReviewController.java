package com.example.gamemate.domain.review.controller;

import com.example.gamemate.domain.review.dto.*;
import com.example.gamemate.domain.review.service.ReviewService;
import com.example.gamemate.domain.user.entity.User;
import com.example.gamemate.domain.user.repository.UserRepository;
import com.example.gamemate.global.config.auth.CustomUserDetails;
import com.example.gamemate.global.constant.ErrorCode;
import com.example.gamemate.global.exception.ApiException;
import com.example.gamemate.global.provider.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import static com.example.gamemate.global.constant.ErrorCode.USER_NOT_FOUND;

@RestController
@RequestMapping("/games/{gameId}/reviews")
@AllArgsConstructor
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;


    /**
     * 리뷰등록
     *
     * @param gameId
     * @param requestDto
     * @param customUserDetails
     * @return
     */
    @PostMapping
    public ResponseEntity<ReviewCreateResponseDto> createReview(
            @PathVariable Long gameId,
            @Valid @RequestBody ReviewCreateRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        ReviewCreateResponseDto responseDto = reviewService.createReview(customUserDetails.getUser(), gameId, requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    /**
     * 리뷰수정
     *
     * @param gameId
     * @param id
     * @param requestDto
     * @param customUserDetails
     * @return
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateReview(
            @PathVariable Long gameId,
            @PathVariable Long id,
            @Valid @RequestBody ReviewUpdateRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        reviewService.updateReview(customUserDetails.getUser(), gameId, id, requestDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * 리뷰삭제
     *
     * @param gameId
     * @param id
     * @param customUserDetails
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long gameId,
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        reviewService.deleteReview(customUserDetails.getUser(), id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<Page<ReviewFindByAllResponseDto>> ReviewFindAllByGameId(
            @PathVariable Long gameId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        Page<ReviewFindByAllResponseDto> responseDto = reviewService.ReviewFindAllByGameId(gameId, customUserDetails.getUser());
        return new ResponseEntity<>(responseDto, HttpStatus.OK);

    }
}

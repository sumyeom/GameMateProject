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
import lombok.RequiredArgsConstructor;
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

/**
 * 게임 리뷰 관련 API를 처리하는 컨트롤러 클래스입니다.
 * 이 컨트롤러는 리뷰의 생성, 수정, 삭제 및 조회 기능을 제공합니다.
 */
@RestController
@RequestMapping("/games/{gameId}/reviews")
@Slf4j
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 새로운 게임 리뷰를 생성합니다.
     *
     * @param gameId 리뷰를 작성할 게임의 ID
     * @param requestDto 리뷰 생성 요청 데이터
     * @param customUserDetails 인증된 사용자 정보
     * @return 생성된 리뷰 정보를 포함한 ResponseEntity
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
     * 기존 게임 리뷰를 수정합니다.
     *
     * @param gameId 리뷰가 속한 게임의 ID
     * @param id 수정할 리뷰의 ID
     * @param requestDto 리뷰 수정 요청 데이터
     * @param customUserDetails 인증된 사용자 정보
     * @return 수정 결과를 나타내는 ResponseEntity
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
     * 게임 리뷰를 삭제합니다.
     *
     * @param gameId 리뷰가 속한 게임의 ID
     * @param id 삭제할 리뷰의 ID
     * @param customUserDetails 인증된 사용자 정보
     * @return 삭제 결과를 나타내는 ResponseEntity
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long gameId,
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        reviewService.deleteReview(customUserDetails.getUser(), id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * 특정 게임의 모든 리뷰를 조회합니다.
     *
     * @param gameId 리뷰를 조회할 게임의 ID
     * @param customUserDetails 인증된 사용자 정보
     * @return 게임의 모든 리뷰 목록을 포함한 ResponseEntity
     */
    @GetMapping
    public ResponseEntity<Page<ReviewFindByAllResponseDto>> ReviewFindAllByGameId(
            @PathVariable Long gameId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        Page<ReviewFindByAllResponseDto> responseDto = reviewService.ReviewFindAllByGameId(gameId, customUserDetails.getUser());
        return new ResponseEntity<>(responseDto, HttpStatus.OK);

    }
}

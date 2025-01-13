package com.example.gamemate.domain.review.controller;

import com.example.gamemate.domain.review.dto.ReviewCreateRequestDto;
import com.example.gamemate.domain.review.dto.ReviewCreateResponseDto;
import com.example.gamemate.domain.review.dto.ReviewUpdateRequestDto;
import com.example.gamemate.domain.review.dto.ReviewUpdateResponseDto;
import com.example.gamemate.domain.review.service.ReviewService;
import com.example.gamemate.domain.user.entity.User;
import com.example.gamemate.domain.user.repository.UserRepository;
import com.example.gamemate.global.config.auth.CustomUserDetails;
import com.example.gamemate.global.constant.ErrorCode;
import com.example.gamemate.global.exception.ApiException;
import com.example.gamemate.global.provider.JwtTokenProvider;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.example.gamemate.global.constant.ErrorCode.USER_NOT_FOUND;

@RestController
@RequestMapping("/games/{gameId}/reviews")
@AllArgsConstructor
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;
    private final JwtTokenProvider jwtTokenProvider;


    /**
     * 게임에 대한 리뷰를 등록합니다.
     *
     * @param gameId     리뷰를 등록할 게임의 고유 식별자
     * @param requestDto 리뷰 정보를 담고 있는 DTO 객체
     * @param token      사용자 인증 토큰
     * @return 등록된 리뷰의 정보
     * @throws UnauthorizedException 유효하지 않은 토큰일 경우
     * @throws GameNotFoundException 해당 gameId의 게임이 존재하지 않을 경우
     */
    @PostMapping
    public ResponseEntity<ReviewCreateResponseDto> createReview(
            @PathVariable Long gameId,
            @RequestBody ReviewCreateRequestDto requestDto,
            @RequestHeader("Authorization") String token) {

        // "Bearer " 접두사 제거
        token = token.substring(7);
        // 토큰에서 이메일 추출
        String email = jwtTokenProvider.getEmailFromToken(token);

        ReviewCreateResponseDto responseDto = reviewService.createReview(email, gameId, requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    /**
     * 리뷰 수정
     *
     * @param gameId
     * @param id
     * @param requestDto
     * @return
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateReview(
            @PathVariable Long gameId,
            @PathVariable Long id,
            @RequestBody ReviewUpdateRequestDto requestDto,
            @RequestHeader("Authorization") String token) {

        // "Bearer " 접두사 제거
        token = token.substring(7);
        // 토큰에서 이메일 추출
        String email = jwtTokenProvider.getEmailFromToken(token);

        reviewService.updateReview(email, gameId, id, requestDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * 리뷰 삭제
     *
     * @param gameId
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long gameId,
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {



        reviewService.deleteReview(userDetails.getUser(), id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

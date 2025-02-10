package com.example.gamemate.domain.follow.controller;

import com.example.gamemate.domain.follow.service.FollowService;
import com.example.gamemate.domain.follow.dto.*;
import com.example.gamemate.global.config.auth.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 팔로우 기능을 처리하는 컨트롤러 클래스입니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/follows")
public class FollowController {

    private final FollowService followService;

    /**
     * 사용자간의 팔로우를 생성을 처리합니다.
     *
     * @param dto FollowCreateRequestDto 팔로우할 상대방의 email
     * @param customUserDetails 현재 인증된 사용자 정보
     * @return 팔로우 처리 결과를 담은 ResponseEntity
     */
    @PostMapping
    public ResponseEntity<FollowResponseDto> createFollow(
            @RequestBody FollowCreateRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {

        FollowResponseDto followResponseDto = followService.createFollow(dto, customUserDetails.getUser());
        return new ResponseEntity<>(followResponseDto, HttpStatus.CREATED);
    }

    /**
     * 사용자간의 팔로우 취소를 처리합니다.
     *
     * @param id 취소할 팔로우 ID
     * @param customUserDetails 현재 인증된 사용자 정보
     * @return 204 NO_CONTENT 성공했지만 반환값이 없음
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFollow(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {

        followService.deleteFollow(id, customUserDetails.getUser());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * 팔로우 상태를 확인합니다. (loginUser 가 followee 를 팔로우 했는지 확인)
     *
     * @param customUserDetails 현재 인증된 사용자 정보
     * @param email 팔로우 상태를 확인할 상대방 이메일
     * @return 로그인한 사용자가 상대방을 팔로우 했을시 true, 아닐시 false
     */
    @GetMapping("/status")
    public ResponseEntity<FollowBooleanResponseDto> findFollow(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam String email
    ) {

        FollowBooleanResponseDto followBooleanResponseDto = followService.findFollow(customUserDetails.getUser(), email);
        return new ResponseEntity<>(followBooleanResponseDto, HttpStatus.OK);
    }

    /**
     * 특정 유저의 팔로워 목록를 조회합니다.
     *
     * @param email 팔로워 목록을 보고 싶은 유저 email
     * @return 특정 유저의 팔로워 목록을 담은 ResponseEntity
     */
    @GetMapping("/followers")
    public ResponseEntity<List<FollowFindResponseDto>> findFollowers(
            @RequestParam String email
    ) {

        List<FollowFindResponseDto> followFindResponseDtoList = followService.findFollowers(email);
        return new ResponseEntity<>(followFindResponseDtoList, HttpStatus.OK);
    }

    /**
     * 특정 유저의 팔로잉 목록을 조회합니다.
     *
     * @param email 팔로잉 목록을 보고 싶은 유저 email
     * @return 특정 유저의 팔로잉 목록을 담은 ResponseEntity
     */
    @GetMapping("/following")
    public ResponseEntity<List<FollowFindResponseDto>> findFollowing(
            @RequestParam String email
    ) {

        List<FollowFindResponseDto> followFindResponseDtoList = followService.findFollowing(email);
        return new ResponseEntity<>(followFindResponseDtoList, HttpStatus.OK);
    }
}

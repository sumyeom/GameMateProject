package com.example.gamemate.domain.follow;

import com.example.gamemate.domain.follow.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/follows")
public class FollowController {

    private final FollowService followService;

    /**
     * 팔로우 하기
     * @param dto FollowCreateRequestDto
     * @return message = "팔로우 했습니다."
     */
    @PostMapping
    public ResponseEntity<FollowResponseDto> createFollow(@RequestBody FollowCreateRequestDto dto) {
        FollowResponseDto followResponseDto = followService.createFollow(dto);
        return new ResponseEntity<>(followResponseDto, HttpStatus.CREATED);
    }

    /**
     * 팔로우 취소
     * @param followId 취소할 팔로우 식별자
     * @return message = "팔로우를 취소했습니다."
     */
    @DeleteMapping("/{followId}")
    public ResponseEntity<FollowResponseDto> deleteFollow(@PathVariable Long followId) {
        FollowResponseDto followResponseDto = followService.deleteFollow(followId);
        return new ResponseEntity<>(followResponseDto,HttpStatus.OK);
    }

    /**
     * 팔로우 상태 확인 (follower 가 followee 를 팔로우 했는지 확인)
     * @param followerEmail
     * @param followeeEmail
     * @return message = "팔로우 중 입니다." or "아직 팔로우 하지 않았습니다."
     */
    @GetMapping("/status")
    public ResponseEntity<FollowResponseDto> findFollow(@RequestParam String followerEmail, @RequestParam String followeeEmail) {
        FollowResponseDto followResponseDto = followService.findFollow(followerEmail, followeeEmail);
        return new ResponseEntity<>(followResponseDto, HttpStatus.OK);
    }

    /**
     * 팔로우 목록 보기
     * @param email 팔로우 목록을 보고 싶은 유저 email
     * @return followerList
     */
    @GetMapping("/follower-list")
    public ResponseEntity<List<FollowFindResponseDto>> findFollowerList(@RequestParam String email) {
        List<FollowFindResponseDto> followerList = followService.findFollowerList(email);
        return new ResponseEntity<>(followerList, HttpStatus.OK);
    }

    /**
     * 팔로잉 목록 보기
     * @param email 팔로잉 목록을 보고 싶은 유저 email
     * @return followingList
     */
    @GetMapping("/following-list")
    public ResponseEntity<List<FollowFindResponseDto>> findFollowingList(@RequestParam String email) {
        List<FollowFindResponseDto> followingList = followService.findFollowingList(email);
        return new ResponseEntity<>(followingList, HttpStatus.OK);
    }
}

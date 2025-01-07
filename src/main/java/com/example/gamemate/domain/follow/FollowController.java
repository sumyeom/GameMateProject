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
    public ResponseEntity<FollowCreateResponseDto> createFollow(@RequestBody FollowCreateRequestDto dto) {
        FollowCreateResponseDto followCreateResponseDto = followService.createFollow(dto);
        return new ResponseEntity<>(followCreateResponseDto, HttpStatus.CREATED);
    }

    /**
     * 팔로우 취소
     * @param followId 취소할 팔로우 식별자
     * @return message = "팔로우를 취소했습니다."
     */
    @DeleteMapping("/{followId}")
    public ResponseEntity<FollowDeleteResponseDto> deleteFollow(@PathVariable Long followId) {
        FollowDeleteResponseDto followDeleteResponseDto = followService.deleteFollow(followId);
        return new ResponseEntity<>(followDeleteResponseDto,HttpStatus.OK);
    }

    /**
     * 팔로우 목록 보기
     * @param dto FollowFindRequestDto
     * @return followerList
     */
    @GetMapping("/follower-list")
    public ResponseEntity<List<FollowFindResponseDto>> findFollowerList(@RequestBody FollowFindRequestDto dto) {
        List<FollowFindResponseDto> followerList = followService.findFollowerList(dto);
        return new ResponseEntity<>(followerList, HttpStatus.OK);
    }
}

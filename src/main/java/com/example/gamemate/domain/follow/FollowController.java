package com.example.gamemate.domain.follow;

import com.example.gamemate.domain.follow.dto.FollowCreateRequestDto;
import com.example.gamemate.domain.follow.dto.FollowCreateResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

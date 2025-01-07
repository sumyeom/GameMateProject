package com.example.gamemate.domain.follow;

import com.example.gamemate.domain.follow.dto.FollowCreateRequestDto;
import com.example.gamemate.domain.follow.dto.FollowCreateResponseDto;
import com.example.gamemate.domain.follow.dto.FollowDeleteResponseDto;
import com.example.gamemate.global.constant.ErrorCode;
import com.example.gamemate.global.exception.ApiException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    // 팔로우하기
    // todo: 현재 로그인이 구현되지 않아 1번유저가 팔로우 하는것으로 구현했으니 추후 로그인이 구현되면 follower 는 로그인한 유저로 설정
    @Transactional
    public FollowCreateResponseDto createFollow(FollowCreateRequestDto dto) {
        User follower = userRepository.findById(1L).orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
        User followee = userRepository.findByEmail(dto.getEmail()).orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        if (followRepository.existsByFollowerAndFollowee(follower, followee)) {
            throw new ApiException(ErrorCode.IS_ALREADY_FOLLOWED);
        }

        if (Objects.equals(follower.getEmail(), dto.getEmail())) {
            throw new ApiException(ErrorCode.INVALID_INPUT);
        }

        Follow follow = new Follow(follower,followee);
        followRepository.save(follow);

        return new FollowCreateResponseDto("팔로우 했습니다.");
    }

    // 팔로우 취소하기
    // todo: 현재 로그인이 구현되지 않아 1번유저가 팔로우를 취소 하는것으로 구현했으니 추후 로그인이 구현되면 follower 는 로그인한 유저로 설정
    @Transactional
    public FollowDeleteResponseDto deleteFollow(Long followId) {
        Follow findFollow = followRepository.findById(followId).orElseThrow(() -> new RuntimeException("팔로우를 찾을 수 없습니다."));

        User follower = userRepository.findById(1L).orElseThrow();

        if (findFollow.getFollower() != follower) {
            throw new ApiException(ErrorCode.INVALID_INPUT);
        }

        followRepository.delete(findFollow);

        return new FollowDeleteResponseDto("팔로우가 취소되었습니다.");
    }
}

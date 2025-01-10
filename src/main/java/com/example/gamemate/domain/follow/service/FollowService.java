package com.example.gamemate.domain.follow.service;

import com.example.gamemate.domain.follow.dto.*;
import com.example.gamemate.domain.follow.entity.Follow;
import com.example.gamemate.domain.follow.repository.FollowRepository;
import com.example.gamemate.domain.user.entity.User;
import com.example.gamemate.domain.user.enums.UserStatus;
import com.example.gamemate.domain.user.repository.UserRepository;
import com.example.gamemate.global.constant.ErrorCode;
import com.example.gamemate.global.exception.ApiException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    // 팔로우하기
    // todo: 현재 로그인이 구현되지 않아 1번유저가 팔로우 하는것으로 구현했으니 추후 로그인이 구현되면 follower 는 로그인한 유저로 설정
    @Transactional
    public FollowResponseDto createFollow(FollowCreateRequestDto dto) {

        User follower = userRepository.findById(1L).orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        User followee = userRepository.findByEmail(dto.getEmail()).orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        if (followee.getUserStatus() == UserStatus.WITHDRAW) {
            throw new ApiException(ErrorCode.IS_WITHDRAW_USER);
        }

        if (followRepository.existsByFollowerAndFollowee(follower, followee)) {
            throw new ApiException(ErrorCode.IS_ALREADY_FOLLOWED);
        }

        if (Objects.equals(follower.getEmail(), dto.getEmail())) {
            throw new ApiException(ErrorCode.INVALID_INPUT);
        }

        Follow follow = new Follow(follower, followee);
        followRepository.save(follow);

        return new FollowResponseDto("팔로우 했습니다.");
    }

    // 팔로우 취소하기
    // todo: 현재 로그인이 구현되지 않아 1번유저가 팔로우를 취소 하는것으로 구현했으니 추후 로그인이 구현되면 follower 는 로그인한 유저로 설정
    @Transactional
    public void deleteFollow(Long id) {

        Follow findFollow = followRepository.findById(id).orElseThrow(() -> new ApiException(ErrorCode.FOLLOW_NOT_FOUND));
        User follower = userRepository.findById(1L).orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        if (findFollow.getFollower() != follower) {
            throw new ApiException(ErrorCode.INVALID_INPUT);
        }

        followRepository.delete(findFollow);
    }

    // 팔로우 상태 확인
    // todo : 로그인한 유저(follower) 기준으로 상대 유저(followee)가 팔로우 되어 있는지 확인이 필요한 것이므로, 로그인 구현시 코드 수정해야함.
    public FollowResponseDto findFollow(String followerEmail, String followeeEmail) {

        User follower = userRepository.findByEmail(followerEmail).orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        User followee = userRepository.findByEmail(followeeEmail).orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        if (followee.getUserStatus() == UserStatus.WITHDRAW) {
            throw new ApiException(ErrorCode.IS_WITHDRAW_USER);
        }

        if (!followRepository.existsByFollowerAndFollowee(follower, followee)) {
            return new FollowResponseDto("아직 팔로우 하지 않았습니다.");
        }

        return new FollowResponseDto("팔로우 중 입니다.");
    }

    // 팔로워 목록보기
    public List<FollowFindResponseDto> findFollowers(String email) {

        User followee = userRepository.findByEmail(email).orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        if (followee.getUserStatus() == UserStatus.WITHDRAW) {
            throw new ApiException(ErrorCode.IS_WITHDRAW_USER);
        }

        List<Follow> followListByFollowee = followRepository.findByFollowee(followee);

        List<User> followersByFollowee = followListByFollowee.stream()
                .map(Follow::getFollower)
                .filter(follower -> follower.getUserStatus() != UserStatus.WITHDRAW)
                .toList();

        return followersByFollowee
                .stream()
                .map(FollowFindResponseDto::toDto)
                .toList();
    }

    // 팔로잉 목록보기
    public List<FollowFindResponseDto> findFollowing(String email) {

        User follower = userRepository.findByEmail(email).orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        if (follower.getUserStatus() == UserStatus.WITHDRAW) {
            throw new ApiException(ErrorCode.IS_WITHDRAW_USER);
        }

        List<Follow> followListByFollower = followRepository.findByFollower(follower);

        List<User> followingByFollower = followListByFollower.stream()
                .map(Follow::getFollowee)
                .filter(followee -> followee.getUserStatus() != UserStatus.WITHDRAW)
                .toList();

        return followingByFollower
                .stream()
                .map(FollowFindResponseDto::toDto)
                .toList();
    }
}

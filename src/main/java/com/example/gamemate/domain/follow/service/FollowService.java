package com.example.gamemate.domain.follow.service;

import com.example.gamemate.domain.follow.dto.*;
import com.example.gamemate.domain.follow.entity.Follow;
import com.example.gamemate.domain.follow.repository.FollowRepository;
import com.example.gamemate.domain.notification.enums.NotificationType;
import com.example.gamemate.domain.notification.service.NotificationService;
import com.example.gamemate.domain.user.entity.User;
import com.example.gamemate.domain.user.enums.UserStatus;
import com.example.gamemate.domain.user.repository.UserRepository;
import com.example.gamemate.global.constant.ErrorCode;
import com.example.gamemate.global.exception.ApiException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class FollowService {
    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final NotificationService notificationService;

    // 팔로우하기
    @Transactional
    public FollowResponseDto createFollow(FollowCreateRequestDto dto, User loginUser) {

        User followee = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        if (followee.getUserStatus() == UserStatus.WITHDRAW) {
            throw new ApiException(ErrorCode.IS_WITHDRAWN_USER);
        } // 탈퇴한 유저일때 예외처리

        if (followRepository.existsByFollowerAndFollowee(loginUser, followee)) {
            throw new ApiException(ErrorCode.IS_ALREADY_FOLLOWED);
        } // 이미 팔로우를 했을때 예외처리

        if (Objects.equals(loginUser.getEmail(), dto.getEmail())) {
            throw new ApiException(ErrorCode.INVALID_INPUT);
        } // 자기 자신을 팔로우 할때 예외처리

        Follow follow = new Follow(loginUser, followee);
        followRepository.save(follow);
        notificationService.createNotification(followee, NotificationType.NEW_FOLLOWER);

        return new FollowResponseDto(
                follow.getId(),
                follow.getFollower().getId(),
                follow.getFollowee().getId(),
                follow.getCreatedAt()
        );
    }

    // 팔로우 취소하기
    @Transactional
    public void deleteFollow(Long id, User loginUser) {

        Follow findFollow = followRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.FOLLOW_NOT_FOUND));

        log.info("사용자 email : {}" , loginUser.getEmail());

        if (findFollow.getFollower() != loginUser) {
            throw new ApiException(ErrorCode.INVALID_INPUT);
        } // 본인의 팔로우가 아닐때 예외처리

        followRepository.delete(findFollow);
    }

    // 팔로우 상태 확인
    public FollowBooleanResponseDto findFollow(User loginUser, String email) {

        User followee = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        if (followee.getUserStatus() == UserStatus.WITHDRAW) {
            throw new ApiException(ErrorCode.IS_WITHDRAWN_USER);
        } // 확인할 상대방이 탈퇴한 회원일때 예외처리

        if (!followRepository.existsByFollowerAndFollowee(loginUser, followee)) {
            return new FollowBooleanResponseDto(
                    false,
                    loginUser.getId(),
                    followee.getId()
            );
        }

        return new FollowBooleanResponseDto(
                true,
                loginUser.getId(),
                followee.getId()
        );
    }

    // 팔로워 목록보기
    public List<FollowFindResponseDto> findFollowers(String email) {

        User followee = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        if (followee.getUserStatus() == UserStatus.WITHDRAW) {
            throw new ApiException(ErrorCode.IS_WITHDRAWN_USER);
        } // 확인할 상대방이 탈퇴한 회원일때 예외처리

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

        User follower = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        if (follower.getUserStatus() == UserStatus.WITHDRAW) {
            throw new ApiException(ErrorCode.IS_WITHDRAWN_USER);
        } // 확인할 상대방이 탈퇴한 회원일때 예외처리

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

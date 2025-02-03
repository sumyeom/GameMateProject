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


/**
 * 팔로우 기능을 처리하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FollowService {
    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final NotificationService notificationService;


    /**
     * 사용자 간의 팔로우를 생성합니다.
     * @param dto FollowCreateRequestDto 팔로우할 상대방의 email
     * @param loginUser 현재 인증된 사용자 정보
     * @return 팔로우 처리 결과를 담은 FollowResponseDto
     */
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

    /**
     * 사용자 간의 팔로우를 취소합니다.
     * @param id 팔로우 id
     * @param loginUser 현재 인증된 사용자 정보
     */
    @Transactional
    public void deleteFollow(Long id, User loginUser) {

        Follow findFollow = followRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.FOLLOW_NOT_FOUND));

        log.info("사용자 email : {}" , loginUser.getEmail());

        if (!Objects.equals(findFollow.getFollower().getId(), loginUser.getId())) {
            throw new ApiException(ErrorCode.INVALID_INPUT);
        } // 본인의 팔로우가 아닐때 예외처리

        followRepository.delete(findFollow);
    }

    /**
     * 팔로우 상태를 확인합니다. (loginUser 가 followee 를 팔로우 했는지 확인)
     * @param loginUser 현재 인증된 사용자 정보
     * @param email 팔로우 상태를 확인할 사용자 email
     * @return 팔로우 상태의 정보를 담은 FollowBooleanResponseDto
     */
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

    /**
     * 특정 유저의 팔로워 목록를 조회합니다.
     * @param email 팔로워 목록을 확인할 상대방 email
     * @return 팔로워 목록을 담은 List<FollowFindResponseDto>
     */
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

    /**
     * 특정 유저의 팔로잉 목록를 조회합니다.
     * @param email 팔로잉 목록을 조회할 상대방 email
     * @return 팔로잉 목록을 담은 List<FollowFindResponseDto>
     */
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

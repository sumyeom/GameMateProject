package com.example.gamemate.global.test;

import com.example.gamemate.domain.follow.entity.Follow;
import com.example.gamemate.domain.follow.repository.FollowRepository;
import com.example.gamemate.domain.user.entity.User;
import com.example.gamemate.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestDataController {
    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    @PostMapping("/init")
    public ResponseEntity<String> initializeTestData() {
        // 타겟 유저 생성
        User targetUser = new User("target@test.com", "TargetUser", "TargetUser", "1234");
        userRepository.save(targetUser);

        // 1000명의 팔로워 생성 및 팔로우 관계 설정
        List<User> followers = new ArrayList<>();
        List<Follow> follows = new ArrayList<>();

        for (int i = 0; i < 1000; i++) {
            User follower = new User(
                    "test" + i + "@test.com",
                    "User" + i,
                    "User" + i,
                    "1234"
            );
            followers.add(follower);
        }

        // 벌크 저장으로 성능 향상
        List<User> savedFollowers = userRepository.saveAll(followers);

        // 팔로우 관계 생성
        for (User follower : savedFollowers) {
            follows.add(new Follow(follower, targetUser));
        }
        followRepository.saveAll(follows);

        return ResponseEntity.ok(String.format(
                "테스트 데이터 생성 완료 (타겟 유저: %s, 팔로워: %d명)",
                targetUser.getEmail(),
                follows.size()
        ));
    }
}
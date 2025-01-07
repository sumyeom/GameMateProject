package com.example.gamemate.domain.follow;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    Boolean existsByFollowerAndFollowee(User follower, User followee);
    List<Follow> findByFollowee(User followee);
    List<Follow> findByFollower(User follower);
}

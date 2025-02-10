package com.example.gamemate.domain.follow.repository;

import com.example.gamemate.domain.follow.dto.FollowFindResponseDto;
import com.example.gamemate.domain.follow.entity.Follow;
import com.example.gamemate.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    Boolean existsByFollowerAndFollowee(User follower, User followee);
    List<Follow> findByFollowee(User followee);
    List<Follow> findByFollower(User follower);

    @Query("SELECT NEW com.example.gamemate.domain.follow.dto.FollowFindResponseDto(f.follower.id, f.follower.nickname) " +
            "FROM Follow f " +
            "JOIN f.follower " +
            "WHERE f.followee.email = :email " +
            "AND f.follower.userStatus != 'WITHDRAW'")
    List<FollowFindResponseDto> findFollowersByFolloweeEmail(@Param("email") String email);

    @Query("SELECT NEW com.example.gamemate.domain.follow.dto.FollowFindResponseDto(f.followee.id, f.followee.nickname) " +
            "FROM Follow f " +
            "JOIN f.followee " +
            "WHERE f.follower.email = :email " +
            "AND f.followee.userStatus != 'WITHDRAW'")
    List<FollowFindResponseDto> findFollowingByFollowerEmail(@Param("email") String email);
}

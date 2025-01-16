package com.example.gamemate.domain.match.repository;

import com.example.gamemate.domain.match.entity.MatchUserInfo;
import com.example.gamemate.domain.match.enums.Gender;
import com.example.gamemate.domain.match.enums.PlayTimeRange;
import com.example.gamemate.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface MatchUserInfoRepository extends JpaRepository<MatchUserInfo, Long> {
    Optional<MatchUserInfo> findByUser(User user);
    Boolean existsByUser(User user);

    @Query("SELECT m FROM MatchUserInfo m WHERE m.gender = :gender AND EXISTS (SELECT pt FROM m.playTimeRanges pt WHERE pt IN :playTimeRanges) AND m.user.id <> :userId")
    List<MatchUserInfo> findByGenderAndPlayTimeRanges(@Param("gender") Gender gender,
                                                      @Param("playTimeRanges") Set<PlayTimeRange> playTimeRanges,
                                                      @Param("userId") Long userId);

}

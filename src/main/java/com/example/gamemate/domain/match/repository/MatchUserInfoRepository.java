package com.example.gamemate.domain.match.repository;

import com.example.gamemate.domain.match.entity.MatchUserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchUserInfoRepository extends JpaRepository<MatchUserInfo, Long> {
}

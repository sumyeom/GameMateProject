package com.example.gamemate.domain.match.repository;

import com.example.gamemate.domain.match.entity.MatchDesiredInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchDesiredInfoRepository extends JpaRepository<MatchDesiredInfo, Long> {
}

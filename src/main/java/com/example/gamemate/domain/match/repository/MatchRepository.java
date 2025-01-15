package com.example.gamemate.domain.match.repository;

import com.example.gamemate.domain.match.entity.Match;
import com.example.gamemate.domain.match.enums.MatchStatus;
import com.example.gamemate.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

    Boolean existsBySenderAndReceiverAndStatus(User sender, User receiver, MatchStatus status);
    List<Match> findAllByReceiverId(Long receiverId);
    List<Match> findAllBySenderId(Long senderId);

}

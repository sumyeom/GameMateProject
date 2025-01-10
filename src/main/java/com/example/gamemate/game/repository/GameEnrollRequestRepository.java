package com.example.gamemate.game.repository;

import com.example.gamemate.game.entity.GamaEnrollRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameEnrollRequestRepository extends JpaRepository<GamaEnrollRequest,Long> {

    List<GamaEnrollRequest> findByIsAccepted(Boolean isAccepted);
}

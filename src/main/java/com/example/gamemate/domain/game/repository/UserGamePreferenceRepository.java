package com.example.gamemate.domain.game.repository;

import com.example.gamemate.domain.game.entity.UserGamePreference;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserGamePreferenceRepository extends JpaRepository<UserGamePreference,Long> {
}

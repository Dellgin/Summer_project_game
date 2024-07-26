package com.game.bullsAndCows.Repositories;

import com.game.bullsAndCows.Entity.Games;
import com.game.bullsAndCows.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameRepository extends JpaRepository<Games, Long> {
    List<Games> findByIdUsersOrderByStartTimeDesc(Users user);
}
package com.game.bullsAndCows.Repositories;

import com.game.bullsAndCows.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByNickname(String nickname);
}

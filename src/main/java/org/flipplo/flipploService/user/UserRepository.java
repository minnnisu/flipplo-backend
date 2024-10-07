package org.flipplo.flipploService.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findBySnsId(String snsId);

    Optional<User> findByNickname(String nickname);
}

package com.smallsquare.modules.user.domain.repository;

import com.smallsquare.modules.user.domain.entity.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository {

    User save(User user);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Optional<User> findByUsername(String username);

    Optional<User> findById(Long userId);

    void deleteById(Long userId);
}

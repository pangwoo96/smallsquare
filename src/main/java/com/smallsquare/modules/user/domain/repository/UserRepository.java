package com.smallsquare.modules.user.domain.repository;

import com.smallsquare.modules.user.domain.entity.User;
import com.smallsquare.modules.user.domain.vo.Email;
import com.smallsquare.modules.user.domain.vo.Nickname;
import com.smallsquare.modules.user.domain.vo.Username;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository {

    User save(User user);

    boolean existsByUsername(Username username);

    boolean existsByEmail(Email email);

    boolean existsByNickname(Nickname nickname);

    Optional<User> findByUsername(Username username);

    Optional<User> findById(Long userId);

    Optional<User> findByEmail(Email email);
}

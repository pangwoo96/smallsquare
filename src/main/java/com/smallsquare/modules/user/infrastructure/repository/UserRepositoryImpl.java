package com.smallsquare.modules.user.infrastructure.repository;

import com.smallsquare.modules.user.domain.entity.User;
import com.smallsquare.modules.user.domain.repository.UserRepository;
import com.smallsquare.modules.user.domain.vo.Email;
import com.smallsquare.modules.user.domain.vo.Nickname;
import com.smallsquare.modules.user.domain.vo.Username;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final JpaUserRepository jpaUserRepository;

    @Override
    public boolean existsByUsername(Username username) {
        return jpaUserRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return jpaUserRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByNickname(Nickname nickname) {
        return jpaUserRepository.existsByNickname(nickname);
    }

    @Override
    public Optional<User> findByUsername(Username username) {
        return jpaUserRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findById(Long userId) {
        return jpaUserRepository.findById(userId);
    }

    @Override
    public User save(User user) {
        return jpaUserRepository.save(user);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return jpaUserRepository.findByEmail(email);
    }
}

package com.smallsquare.user.infrastructure.repository;

import com.smallsquare.user.domain.entity.User;
import com.smallsquare.user.web.dto.request.UserSignupReqDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);
}

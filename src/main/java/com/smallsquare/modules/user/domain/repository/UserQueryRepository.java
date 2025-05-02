package com.smallsquare.modules.user.domain.repository;

import com.smallsquare.modules.user.web.dto.response.UserInfoResDto;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserQueryRepository {

    Optional<UserInfoResDto> findUserInfoByUserId(Long userId);

}

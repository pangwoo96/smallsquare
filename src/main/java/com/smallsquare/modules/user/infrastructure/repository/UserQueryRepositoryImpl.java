package com.smallsquare.modules.user.infrastructure.repository;

import com.querydsl.core.QueryFactory;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.smallsquare.modules.user.domain.entity.QUser;
import com.smallsquare.modules.user.domain.repository.UserQueryRepository;
import com.smallsquare.modules.user.web.dto.response.UserInfoResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserQueryRepositoryImpl implements UserQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<UserInfoResDto> findUserInfoByUserId(Long userId) {
        QUser user = QUser.user;

        return Optional.ofNullable(
                jpaQueryFactory
                        .select(Projections.constructor(UserInfoResDto.class, // DTO의 생성자를 통해 값을 채움
                                user.username,
                                user.nickname,
                                user.email,
                                user.name
                        ))
                        .from(user) // sql의 from과 동일 (테이블)
                        .where(user.id.eq(userId)) // sql의 where과 동일
                        .fetchOne() // 결과를 1개만 조회
        );
    }
}

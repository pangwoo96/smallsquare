package com.smallsquare.modules.user.application.service;

import com.smallsquare.common.exception.errorCode.UserErrorCode;
import com.smallsquare.common.exception.exception.UserException;
import com.smallsquare.modules.user.web.dto.request.UserLogoutReqDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

import static com.smallsquare.common.exception.errorCode.UserErrorCode.ACCESS_TOKEN_EXPIRED;
import static com.smallsquare.common.exception.errorCode.UserErrorCode.REFRESH_TOKEN_EXPIRED;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    public void saveBlacklist(String accessToken, long accessTokenExpireTime,
                              String refreshToken, long refreshTokenExpireTime) {

        redisTemplate.opsForValue().set(
                "blacklist:access:" + accessToken,
                "logout",
                Duration.ofMillis(accessTokenExpireTime)
        );

        redisTemplate.opsForValue().set(
                "blacklist:refresh:" + refreshToken,
                "logout",
                Duration.ofMillis(refreshTokenExpireTime)
        );
    }

    public void validateAccessToken(String accessToken) {
        if (redisTemplate.hasKey("blacklist:access:" + accessToken)) {
            throw new UserException(ACCESS_TOKEN_EXPIRED);
        }
    }

    public void validateRefreshToken(String refreshToken) {
        if (redisTemplate.hasKey("blacklist:refresh:" + refreshToken)) {
            throw new UserException(REFRESH_TOKEN_EXPIRED);
        }
    }
}
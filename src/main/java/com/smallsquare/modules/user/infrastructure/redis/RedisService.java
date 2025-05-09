package com.smallsquare.modules.user.infrastructure.redis;

import com.smallsquare.common.exception.exception.UserException;
import com.smallsquare.modules.user.web.dto.request.MailReqDto;
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

    /**
     * 로그아웃 시 토큰을 Redis에 BlackList로 저장
     * @param accessToken
     * @param accessTokenExpireTime
     * @param refreshToken
     * @param refreshTokenExpireTime
     */

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

    public void saveRefreshBlackList(String refreshToken, long refreshTokenExpireTime) {
        redisTemplate.opsForValue().set(
                "blacklist:refresh:" + refreshToken,
                "refresh",
                Duration.ofMinutes(refreshTokenExpireTime)
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

    public void set(String key, String value, long expirationSeconds) {
        redisTemplate.opsForValue().set(key, value, Duration.ofMinutes(expirationSeconds));
    }

    // 토큰을 기반으로 Redis에서 값(email) 조회
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // Redis에서 해당 key 삭제
    public void delete(String key) {
        redisTemplate.delete(key);
    }

}
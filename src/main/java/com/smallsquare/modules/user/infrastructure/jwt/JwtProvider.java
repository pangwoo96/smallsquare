package com.smallsquare.modules.user.infrastructure.jwt;

import com.smallsquare.modules.user.web.dto.request.JwtTokenReqDto;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component // 빈 등록
public class JwtProvider {

    private final SecretKey secretKey;
    private final JwtParser jwtParser;

    private static final String NICKNAME_CLAIM = "nickname";
    private static final String EMAIL_CLAIM = "email";
    private static final String NAME_CLAIM = "name";
    private static final String TYPE = "type";

    @Value("${JWT_ACCESS_EXPIRATION}")
    private Long accessTokenExpiration;

    @Value("${JWT_REFRESH_EXPIRATION}")
    private Long refreshTokenExpiration;


    public JwtProvider(@Value("${JWT_SECRET_KEY}") String secret) {
        this.secretKey = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8),
                SignatureAlgorithm.HS256.getJcaName()
        );
        this.jwtParser = Jwts.parser().verifyWith(secretKey).build();
    }

    public String createToken(JwtTokenReqDto reqDto, Long expiredMs, String type) {
        Date now = new Date();
        return Jwts.builder()
                .subject(reqDto.getUsername())
                .claim(NICKNAME_CLAIM, reqDto.getNickname())
                .claim(EMAIL_CLAIM, reqDto.getEmail())
                .claim(NAME_CLAIM, reqDto.getName())
                .claim(TYPE, type)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    /**
     * Access Token 생성 로직
     * @param dto
     * @return AccessToken
     */
    public String createAccessToken(JwtTokenReqDto dto) {
        return createToken(dto, accessTokenExpiration, "access");
    }

    /**
     * Refresh Token 생성 로직
     * @param dto
     * @return Refresh Token
     */
    public String createRefreshToken(JwtTokenReqDto dto) {
        return createToken(dto, refreshTokenExpiration, "refresh");
    }
}

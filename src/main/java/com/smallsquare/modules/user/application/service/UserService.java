package com.smallsquare.modules.user.application.service;

import com.smallsquare.common.exception.exception.UserException;
import com.smallsquare.modules.user.domain.entity.User;
import com.smallsquare.modules.user.infrastructure.jwt.JwtProvider;
import com.smallsquare.modules.user.infrastructure.jwt.JwtUtil;
import com.smallsquare.modules.user.infrastructure.repository.UserRepository;
import com.smallsquare.modules.user.web.dto.request.UserLogoutReqDto;
import com.smallsquare.modules.user.web.dto.request.JwtTokenReqDto;
import com.smallsquare.modules.user.web.dto.request.UserLoginReqDto;
import com.smallsquare.modules.user.web.dto.request.UserSignupReqDto;
import com.smallsquare.modules.user.web.dto.response.UserLoginResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.smallsquare.common.exception.errorCode.UserErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RedisService redisService;
    private final JwtUtil jwtUtil;

    /**
     * 회원가입
     * @param: UserSignupReqDto
     * @Returns: 201 Created
     */
    @Transactional
    public void signup(UserSignupReqDto reqDto) {
        // 1. username, nickname, email 중복검사
        validateDuplicate(reqDto);

        // 2. 비밀번호와 비밀번호 확인이 일치하는지 확인
        validatePasswordMatch(reqDto);

        // 3. DTO의 필드와 비밀번호를 인코딩해서 User 객체로 변환 후 저장
        User user = User.of(reqDto, passwordEncoder.encode(reqDto.getPassword()));
        userRepository.save(user);
    }

    private void validatePasswordMatch(UserSignupReqDto reqDto) {
        if (!reqDto.getPassword().equals(reqDto.getCheckPassword())) {
            throw new UserException(PASSWORD_MISMATCH);
        }
    }

    /**
     * 로그인
     * @param reqDto
     * @return resDto
     */
    @Transactional
    public UserLoginResDto login(UserLoginReqDto reqDto) {

        // 1. 유저조회
        User user = userRepository.findByUsername(reqDto.getUsername())
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        // 2. 비밀번호가 입력한 것과 DB의 비밀번호가 일치하는지 확인
        validatePassword(reqDto, user);

        // 3. 토큰 생성
        JwtTokenReqDto tokenReqDto = createTokenDto(user);

        String accessToken = jwtProvider.createAccessToken(tokenReqDto);
        String refreshToken = jwtProvider.createRefreshToken(tokenReqDto);
        return UserLoginResDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

    }

    /**
     * 로그아웃
     * @note 로그아웃 때 input으로 온 토큰을 redis에 blackList로 저장
     * 이후 인증이나 토큰 재발급 때 검증 로직을 추가
     * @param reqDto
     */
    public void logout(UserLogoutReqDto reqDto) {

        // 1. 토큰 추출
        String accessToken = reqDto.getAccessToken();
        String refreshToken = reqDto.getRefreshToken();

        // 2. Access Token과 Refresh Token의 남은 만료시간을 추출
        long remainTimeAccessToken = jwtUtil.getRemainingExpirationMillis(accessToken);
        long remainTimeRefreshToken = jwtUtil.getRemainingExpirationMillis(refreshToken);

        // 3. RedisService에서 Redis에 블랙리스트로 저장
        redisService.saveBlacklist(accessToken, remainTimeAccessToken, refreshToken, remainTimeRefreshToken);
    }






    /**
     *  username, nickname, email 중복검사
     * @param reqDto
     */
    private void validateDuplicate(UserSignupReqDto reqDto) {
        if (userRepository.existsByUsername(reqDto.getUsername())) {
            throw new UserException(DUPLICATED_USERNAME);
        }
        if (userRepository.existsByEmail(reqDto.getEmail())) {
            throw new UserException(DUPLICATED_EMAIL);
        }
        if (userRepository.existsByNickname(reqDto.getNickname())) {
            throw new UserException(DUPLICATED_NICKNAME);
        }
    }

    /**
     * 입력한 비밀번호와 DB의 비밀번호가 서로 일치하는지 확인
     * @note mathes()는 인코딩 안된 평문 비밀번호와 인코딩된 비밀번호를 비교하도록 설계된 메소드
     * @param reqDto
     * @param user
     */
    private void validatePassword(UserLoginReqDto reqDto, User user) {
        if (!passwordEncoder.matches(reqDto.getPassword(), user.getPassword())) {
            throw new UserException(PASSWORD_NOT_MATCHED);
        }
    }

    /**
     * TokenDto로 변환하는 메소드
     * @param user
     * @return
     */
    private JwtTokenReqDto createTokenDto(User user) {
        return JwtTokenReqDto.builder()
                .username(user.getUsername())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .build();
    }
}

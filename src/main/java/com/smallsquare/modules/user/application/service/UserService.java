package com.smallsquare.modules.user.application.service;

import com.smallsquare.common.exception.exception.UserException;
import com.smallsquare.modules.user.domain.entity.User;
import com.smallsquare.modules.user.domain.repository.UserQueryRepository;
import com.smallsquare.modules.user.domain.repository.UserRepository;
import com.smallsquare.modules.user.infrastructure.jwt.JwtProvider;
import com.smallsquare.modules.user.infrastructure.jwt.JwtUtil;
import com.smallsquare.modules.user.infrastructure.redis.RedisService;
import com.smallsquare.modules.user.infrastructure.repository.JpaUserRepository;
import com.smallsquare.modules.user.web.dto.request.*;
import com.smallsquare.modules.user.web.dto.response.UserInfoResDto;
import com.smallsquare.modules.user.web.dto.response.UserLoginResDto;
import com.smallsquare.modules.user.web.dto.response.UserUpdateResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
    private final UserQueryRepository userQueryRepository;

    /**
     * 회원가입
     * @param: UserSignupReqDto
     * @Returns: void
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
    @Transactional
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
     * 내 정보 조회
     * QueryDSL을 통해 사용자 정보를 조회
     * @param userId
     * @return UserInfoResDto (username, name, email, nickname)
     */
    public UserInfoResDto me(Long userId) {
         return userQueryRepository.findUserInfoByUserId(userId)
                 .orElseThrow(() -> new UserException(USER_NOT_FOUND));
    }

    /**
     * 회원정보 수정
     * @param userId, reqDto (username, name, email, nickname)
     * @return UserUpdateResDto (username, name, email, nickname)
     */
    @Transactional
    public UserUpdateResDto updateUserInfo(Long userId, UserUpdateReqDto reqDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException(USER_NOT_FOUND)); // db에서 데이터를 가져와서 1차 캐시에 저장 (영속상태)
        // 영속 객체의 필드 값 변경 (더티 체킹 발생) -> Transactional 범위가 끝날 때 변경 감지 -> 감지되면 update 쿼리 실행
        user.updateInfo(reqDto);

        UserUpdateResDto resDto = UserUpdateResDto.builder()
                .username(user.getUsername())
                .name(user.getName())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .build();

        return resDto;
    }

    /**
     * username, nickname, email 중복검사
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
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .build();
    }

    /**
     * 비밀번호 & 비밀번호 확인이 서로 일치하는지 확인하는 로직
     * @param reqDto
     */
    private void validatePasswordMatch(UserSignupReqDto reqDto) {
        if (!reqDto.getPassword().equals(reqDto.getCheckPassword())) {
            throw new UserException(PASSWORD_MISMATCH);
        }
    }

}

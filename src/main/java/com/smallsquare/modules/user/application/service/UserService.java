package com.smallsquare.modules.user.application.service;

import com.smallsquare.modules.user.domain.entity.User;
import com.smallsquare.modules.user.domain.enums.IsActive;
import com.smallsquare.modules.user.domain.repository.UserQueryRepository;
import com.smallsquare.modules.user.domain.repository.UserRepository;
import com.smallsquare.modules.user.domain.vo.*;
import com.smallsquare.modules.user.exception.exception.UserException;
import com.smallsquare.modules.user.infrastructure.jwt.JwtProvider;
import com.smallsquare.modules.user.infrastructure.jwt.JwtUtil;
import com.smallsquare.modules.user.infrastructure.redis.RedisService;
import com.smallsquare.modules.user.web.dto.request.*;
import com.smallsquare.modules.user.web.dto.response.UserInfoResDto;
import com.smallsquare.modules.user.web.dto.response.UserLoginResDto;
import com.smallsquare.modules.user.web.dto.response.UserUpdateResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.smallsquare.modules.user.exception.errorCode.UserErrorCode.*;

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

        // 1. 비밀번호와 비밀번호 확인이 일치하는지 확인 (평문)
        validateSamePassword(reqDto.getPassword(), reqDto.getCheckPassword());

        // 2. 이메일 인증이 이루어졌는지 확인
        // TODO: PostMan 테스트 시 너무 불편해서 주석처리 한 다음 실제 서비스할 때는 주석 제거
        // verifyEmail(reqDto.getEmail());

        // 3. 비밀번호 객체를 만들어 인코딩 후 대입
        Password password = new Password(reqDto.getPassword(), passwordEncoder);

        // 4. 이메일 객체를 만들어 중복검사
        Email email = new Email(reqDto.getEmail());
        if (userRepository.existsByEmail(email)) {
            throw new UserException(DUPLICATED_EMAIL);
        }

        // 5. username 객체 만들어 중복 검사
        Username username = new Username(reqDto.getUsername());
        if (userRepository.existsByUsername(username)) {
            throw new UserException(DUPLICATED_USERNAME);
        }

        // 6. nickname 객체를 만들어 중복검사
        Nickname nickname = new Nickname(reqDto.getNickname());
        if (userRepository.existsByNickname(nickname)) {
            throw new UserException(DUPLICATED_NICKNAME);
        }

        // 7. name 객체 생성
        Name name = new Name(reqDto.getName());

        // 8. DTO의 필드 User 객체로 변환 후 저장
        User user = User.of(username, password, email, nickname, name);
        userRepository.save(user);
    }

    /**
     * 로그인
     * @param reqDto
     * @return resDto
     */
    @Transactional
    public UserLoginResDto login(UserLoginReqDto reqDto) {

        // 1. username 객체 생성
        Username username = new Username(reqDto.getUsername());

        // 2. 유저조회
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        // 3. 탈퇴한 유저인지 검증
        if (user.getIsActive() == IsActive.INACTIVE) {
            throw new UserException(INACTIVE_ACCOUNT);
        }

        // 4. 비밀번호가 입력한 것과 DB의 비밀번호가 일치하는지 확인
        if (!user.getPassword().matchPassword(reqDto.getPassword(), passwordEncoder)) {
            throw new UserException(PASSWORD_NOT_MATCHED);
        }

        // 5. 토큰 생성
        JwtTokenReqDto tokenReqDto = jwtUtil.createTokenDto(user);

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

        // 1. 해당 회원 조회
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException(USER_NOT_FOUND)); // db에서 데이터를 가져와서 1차 캐시에 저장 (영속상태)

        // 2. 이메일 객체를 만들어 중복검사
        Email email = new Email(reqDto.getEmail());
        if (userRepository.existsByEmail(email) && !user.getEmail().equals(email)) {
            throw new UserException(DUPLICATED_EMAIL);
        }

        // 3. username 객체 만들어 중복 검사
        Username username = new Username(reqDto.getUsername());
        if (userRepository.existsByUsername(username) && !user.getUsername().equals(username)) {
            throw new UserException(DUPLICATED_USERNAME);
        }

        // 4. nickname 객체를 만들어 중복검사
        Nickname nickname = new Nickname(reqDto.getNickname());
        if (userRepository.existsByNickname(nickname) && !user.getNickname().equals(nickname)) {
            throw new UserException(DUPLICATED_NICKNAME);
        }

        // 5. Name 객체 생성
        Name name = new Name(reqDto.getName());

        // 6. 영속 객체의 필드 값 변경 (더티 체킹 발생) -> Transactional 범위가 끝날 때 변경 감지 -> 감지되면 update 쿼리 실행
        user.updateInfo(reqDto, username, email, nickname, name);

        // 7. 반환
        UserUpdateResDto resDto = UserUpdateResDto.builder()
                .username(user.getUsername().getUsername())
                .name(user.getName().getName())
                .email(user.getEmail().getEmail())
                .nickname(user.getNickname().getNickname())
                .build();

        return resDto;
    }

    /**
     * 회원 탈퇴 기능
     * @param userId, reqDto
     */
    @Transactional
    public void deleteUser(Long userId, UserDeleteReqDto reqDto) {

        User user = userRepository.findById(userId).orElseThrow(() -> new UserException(USER_NOT_FOUND));

        // 1. 비밀번호가 일치하는지 확인
        if (!user.getPassword().matchPassword(reqDto.getPassword(), passwordEncoder)) {
            throw new UserException(PASSWORD_NOT_MATCHED);
        }

        // 2. User 테이블의 isActive를 INACTIVE로 변경
        user.deactivate();

    }

    @Transactional
    public void findAndChangePassword(UserFindPasswordReqDto reqDto) {

        // 1. 비밀번호 일치 여부 확인
        validateSamePassword(reqDto.getPassword(), reqDto.getCheckPassword());

        // 2. redis키로 이메일을 추출
        String redisKey = "findPassword:token:" + reqDto.getPasswordToken();
        String emailKey = redisService.get(redisKey);

        // 3. Email 객체 생성
        Email email = new Email(emailKey);

        // 3. 해당 email을 가지고 있는 User 조회
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserException(USER_NOT_FOUND));

        // 4. 이전 비밀번호와 동일하다면 예외 발생
        if (user.getPassword().isSamePassword(reqDto.getPassword(), passwordEncoder)) {
            throw new UserException(SAME_AS_OLD_PASSWORD);
        }

        // 5. 비밀번호 변경
        user.updatePassword(reqDto.getPassword(), passwordEncoder);

        // 6. redis에 해당 키 삭제
        redisService.delete(redisKey);

    }

    /**
     * 비밀번호 변경
     * @param userId
     * @param reqDto
     */
    @Transactional
    public void changePassword(Long userId, UserPasswordChangeReqDto reqDto) {

        // 1. 입력한 비밀번호가 DB의 비밀번호와 일치하는지 확인
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException(USER_NOT_FOUND));
        if (!user.getPassword().matchPassword(reqDto.getPassword(), passwordEncoder)) {
            throw new UserException(PASSWORD_NOT_MATCHED);
        }

        // 2. 새로 입력한 비밀번호와 비밀번호 확인이 서로 일치하는지 확인
        validateSamePassword(reqDto.getNewPassword(), reqDto.getCheckNewPassword());

        // 3. 기존 비밀번호와 새로 입력한 비밀번호가 같은 경우
        if (user.getPassword().isSamePassword(reqDto.getNewPassword(), passwordEncoder)) {
            throw new UserException(SAME_AS_OLD_PASSWORD);
        }

        // 4. 유저의 비밀번호를 새로운 비밀번호로 변경
        user.updatePassword(reqDto.getNewPassword(), passwordEncoder);
    }

    /**
     * 토큰 재발급
     * @param refreshToken
     * @return UserLoginResDto (AccessToken, RefreshToken)
     */
    @Transactional
    public UserLoginResDto refreshToken(String refreshToken) {

        // 1. RefreshToken 검증
        jwtUtil.validateToken(refreshToken);

        // 2. RefreshToken이 블랙리스트인지 확인
        jwtUtil.validateRefreshTokenBlackList(refreshToken);

        // 3. user 조회
        User user = userRepository.findById(jwtUtil.getUserId(refreshToken))
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        // 4. 탈퇴한 사용자인지 검증
        if (user.getIsActive() == IsActive.INACTIVE) {
            throw new UserException(INACTIVE_ACCOUNT);
        }

        // 5. 사용된 리프레시 토큰을 블랙리스트로 저장
        redisService.saveRefreshBlackList(refreshToken, jwtUtil.getRemainingExpirationMillis(refreshToken));

        // 6. Token에 사용자 정보를 담기
        JwtTokenReqDto userInfo = jwtUtil.createTokenDto(user);

        // 7. 새로운 엑세스 토큰과 리프레시 토큰을 만들고 반환
        String newAccessToken = jwtProvider.createAccessToken(userInfo);
        String newRefreshToken = jwtProvider.createRefreshToken(userInfo);

        return UserLoginResDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();

    }



    // ==================== 검증 & 편의 메소드 ==================== //

    /**
     * 비밀번호와 비밀번호 확인이 일치하는지 검증
     * @param pw1
     * @param pw2
     */
    private void validateSamePassword(String pw1, String pw2) {
        if (!pw1.equals(pw2)) {
            throw new UserException(PASSWORD_MISMATCH);
        }
    }

    /**
     * 이메일 인증을 확인하는 로직
     * @param email
     * 개발 단계에서는 잠시 미사용 (회원 가입 시 매번 redis에 값을 넣어줘야해서 번거로움)
     */
    private void verifyEmail(String email) {
        String status = redisService.get("verifyEmail:email:" + email);
        if(!"true".equals(status)) {
            throw new UserException(EMAIL_NOT_VERIFIED);
        }
    }

}

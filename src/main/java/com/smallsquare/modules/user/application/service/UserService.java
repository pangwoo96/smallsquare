package com.smallsquare.modules.user.application.service;

import com.smallsquare.common.exception.exception.UserException;
import com.smallsquare.modules.user.domain.entity.User;
import com.smallsquare.modules.user.infrastructure.jwt.JwtProvider;
import com.smallsquare.modules.user.infrastructure.repository.UserRepository;
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

    /**
     * @param: UserSignupReqDto
     * @Returns: 201 Created
     */
    @Transactional
    public void signup(UserSignupReqDto reqDto) {
        // 1. username, nickname, email 중복검사
        validateDuplicate(reqDto);

        // 2. DTO의 필드와 비밀번호를 인코딩해서 User 객체로 변환 후 저장
        User user = User.of(reqDto, passwordEncoder.encode(reqDto.getPassword()));
        userRepository.save(user);
    }

    /**
     *
     * @param reqDto
     * @return resDto
     */
    public UserLoginResDto login(UserLoginReqDto reqDto) {

        // 1. 유저조회
        User user = userRepository.findByUsername(reqDto.getUsername())
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        // 2. 비밀번호 검증
        // mathes()는 인코딩 안된 평문 비밀번호와 인코딩된 비밀번호를 비교하도록 설계된 메소드
        if (!passwordEncoder.matches(reqDto.getPassword(), user.getPassword())) {
            throw new UserException(PASSWORD_NOT_MATCHED);
        }

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

package com.smallsquare.modules.user.application.service;

import com.smallsquare.common.exception.exception.UserException;
import com.smallsquare.modules.user.domain.entity.User;
import com.smallsquare.modules.user.infrastructure.repository.UserRepository;
import com.smallsquare.modules.user.web.dto.request.UserSignupReqDto;
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

    /**
     * @param: UserSignupReqDto
     * @Returns: 201 Created
     */
    @Transactional
    public void signup(UserSignupReqDto reqDto) {
        // username, nickname, email 중복검사
        validateDuplicate(reqDto);

        // DTO의 필드와 비밀번호를 인코딩해서 User 객체로 변환 후 저장
        User user = User.of(reqDto, passwordEncoder.encode(reqDto.getPassword()));
        userRepository.save(user);
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
}

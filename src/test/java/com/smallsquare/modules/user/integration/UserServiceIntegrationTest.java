package com.smallsquare.modules.user.integration;

import com.smallsquare.common.exception.exception.UserException;
import com.smallsquare.modules.user.application.service.UserService;
import com.smallsquare.modules.user.domain.entity.User;
import com.smallsquare.modules.user.infrastructure.repository.UserRepository;
import com.smallsquare.modules.user.web.dto.request.UserLoginReqDto;
import com.smallsquare.modules.user.web.dto.request.UserSignupReqDto;
import com.smallsquare.modules.user.web.dto.response.UserLoginResDto;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Order(n)에 따라 순서 보장
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Test
    @Order(1)
    void 회원가입_성공() {

        // given
        UserSignupReqDto reqDto = UserSignupReqDto.builder()
                .username("username1")
                .password("password1")
                .checkPassword("password1")
                .nickname("nickname1")
                .email("email1@gmail.com")
                .name("name1")
                .build();

        // when
        userService.signup(reqDto);
        Optional<User> savedUser = userRepository.findByUsername("username1");

        // then
        assertEquals(reqDto.getUsername(), savedUser.get().getUsername());
        assertTrue(passwordEncoder.matches(reqDto.getPassword(), savedUser.get().getPassword()));
        assertEquals(reqDto.getNickname(), savedUser.get().getNickname());
        assertEquals(reqDto.getEmail(), savedUser.get().getEmail());
        assertEquals(reqDto.getName(), savedUser.get().getName());
        assertEquals(reqDto.getRole(), savedUser.get().getRole());
    }

    @Test
    @Order(2)
    void 회원가입_실패_username_중복_확인() {

        // given
        UserSignupReqDto reqDto = UserSignupReqDto.builder()
                .username("username1")
                .password("password1")
                .checkPassword("password1")
                .nickname("nickname1")
                .email("email1@gmail.com")
                .name("name1")
                .build();

        userService.signup(reqDto);

        UserSignupReqDto newReqDto = UserSignupReqDto.builder()
                .username("username1") // 중복되게 설정
                .password("password2")
                .checkPassword("password2")
                .nickname("nickname2")
                .email("email2@gmail.com")
                .name("name2")
                .build();

        // when & then
        assertThrows(UserException.class, () -> userService.signup(newReqDto));
    }

    @Test
    @Order(3)
    void 회원가입_실패_nickname_중복_확인() {

        // given
        UserSignupReqDto reqDto = UserSignupReqDto.builder()
                .username("username1")
                .password("password1")
                .checkPassword("password1")
                .nickname("nickname1")
                .email("email1@gmail.com")
                .name("name1")
                .build();

        userService.signup(reqDto);

        UserSignupReqDto newReqDto = UserSignupReqDto.builder()
                .username("username2")
                .password("password2")
                .checkPassword("password2")
                .nickname("nickname1") // 중복되게 설정
                .email("email2@gmail.com")
                .name("name2")
                .build();

        // when & then
        assertThrows(UserException.class, () -> userService.signup(newReqDto));
    }

    @Test
    @Order(3)
    void 회원가입_실패_email_중복_확인() {

        // given
        UserSignupReqDto reqDto = UserSignupReqDto.builder()
                .username("username1")
                .password("password1")
                .checkPassword("password1")
                .nickname("nickname1")
                .email("email1@gmail.com")
                .name("name1")
                .build();

        userService.signup(reqDto);

        UserSignupReqDto newReqDto = UserSignupReqDto.builder()
                .username("username2")
                .password("password2")
                .checkPassword("password2")
                .nickname("nickname2")
                .email("email1@gmail.com") // 중복되게 설정
                .name("name2")
                .build();

        // when & then
        assertThrows(UserException.class, () -> userService.signup(newReqDto));
    }

    @Test
    @Order(4)
    void 회원가입_실패_비밀번호_불일치() {

        // given
        UserSignupReqDto reqDto = UserSignupReqDto.builder()
                .username("username1")
                .password("password1")
                .checkPassword("password2")
                .nickname("nickname1")
                .email("email1@gmail.com")
                .name("name1")
                .build();

        // when & then
        assertThrows(UserException.class, () -> userService.signup(reqDto));
    }

    @Test
    @Order(5)
    void 로그인_성공() {

        // given
        UserSignupReqDto reqDto = UserSignupReqDto.builder()
                .username("username1")
                .password("password1")
                .checkPassword("password1")
                .nickname("nickname1")
                .email("email1@gmail.com")
                .name("name1")
                .build();

        userService.signup(reqDto);

        UserLoginReqDto loginReqDto = UserLoginReqDto.builder()
                .username("username1")
                .password("password1")
                .build();

        // when
        UserLoginResDto resDto = userService.login(loginReqDto);

        // then
        assertNotNull(resDto.getAccessToken());
        assertNotNull(resDto.getRefreshToken());
        assertTrue(resDto.getAccessToken().startsWith("ey"));
        assertTrue(resDto.getRefreshToken().startsWith("ey"));
    }

    @Test
    @Order(6)
    void 로그인_실패_회원_없음() {
        UserLoginReqDto loginReqDto = UserLoginReqDto.builder()
                .username("username1")
                .password("password1")
                .build();

        // when & then
        assertThrows(UserException.class, () -> userService.login(loginReqDto));
    }

    @Test
    @Order(7)
    void 로그인_실패_비밀번호_불일치() {

        // given
        UserSignupReqDto reqDto = UserSignupReqDto.builder()
                .username("username1")
                .password("password1")
                .checkPassword("password1")
                .nickname("nickname1")
                .email("email1@gmail.com")
                .name("name1")
                .build();

        userService.signup(reqDto);

        UserLoginReqDto loginReqDto = UserLoginReqDto.builder()
                .username("username1")
                .password("password2") // 비밀번호 불일치
                .build();

        // when & then
        assertThrows(UserException.class, () -> userService.login(loginReqDto));
    }

}

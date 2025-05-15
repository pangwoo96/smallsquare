package com.smallsquare.modules.user.integration;

import com.smallsquare.modules.user.exception.exception.UserException;
import com.smallsquare.modules.user.application.service.UserService;
import com.smallsquare.modules.user.domain.entity.User;
import com.smallsquare.modules.user.domain.repository.UserRepository;
import com.smallsquare.modules.user.infrastructure.redis.RedisService;
import com.smallsquare.modules.user.infrastructure.repository.JpaUserRepository;
import com.smallsquare.modules.user.web.dto.request.*;
import com.smallsquare.modules.user.web.dto.response.UserLoginResDto;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
    private JpaUserRepository jpaUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisService redisService;

    // 공통 유틸 함수로 분리
    private Long createAndGetUserId(String username) {
        UserSignupReqDto reqDto = UserSignupReqDto.builder()
                .username(username)
                .password("password1")
                .checkPassword("password1")
                .nickname("nickname1")
                .email("email1@test.com")
                .name("name1")
                .build();

        redisService.set("verifyEmail:email:" + reqDto.getEmail(), "true", 5L);

        userService.signup(reqDto);
        return jpaUserRepository.findByUsername(username).get().getId();
    }

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

        redisService.set("verifyEmail:email:" + reqDto.getEmail(), "true", 5L);
        // when
        userService.signup(reqDto);
        Optional<User> savedUser = jpaUserRepository.findByUsername("username1");

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

    @Test
    @Order(8)
    void 내_정보_조회_성공() {

        Long userId = createAndGetUserId("testuser");

        UserUpdateReqDto updateReqDto = UserUpdateReqDto.builder()
                .username("newUsername")
                .name("newName")
                .email("newEmail@test.com")
                .nickname("newNickname")
                .build();

        // then
        userService.updateUserInfo(userId, updateReqDto);
        User updatedUser = jpaUserRepository.findById(userId).orElseThrow();

        assertEquals("newUsername", updatedUser.getUsername());
        assertEquals("newName", updatedUser.getName());
        assertEquals("newEmail@test.com", updatedUser.getEmail());
        assertEquals("newNickname", updatedUser.getNickname());

    }

    @Test
    @Order(9)
    void 내_정보_조회_실패_없는_유저() {
        Long nonExistentUserId = 9999L;

        UserUpdateReqDto updateDto = UserUpdateReqDto.builder()
                .username("newUsername")
                .name("newName")
                .email("newEmail@test.com")
                .nickname("newNick")
                .build();

        assertThrows(UserException.class, () -> userService.updateUserInfo(nonExistentUserId, updateDto));
    }

    @Test
    @Order(10)
    void 회원_탈퇴_성공() {

        // given
        Long userId = createAndGetUserId("testuser");

        User user = userRepository.findById(userId).orElseThrow();

        UserDeleteReqDto reqDto = UserDeleteReqDto.builder()
                .password("password1")
                .build();

        //when
        userService.deleteUser(userId, reqDto);

        // then
        assertEquals(false, user.getIsActive());
    }

    @Test
    @Order(11)
    void 회원_탈퇴_실패_비밀번호_불일치() {

        // given
        Long userId = createAndGetUserId("testuser");

        User user = userRepository.findById(userId).orElseThrow();

        UserDeleteReqDto reqDto = UserDeleteReqDto.builder()
                .password("password2")
                .build();

        //when & then
        assertThrows(UserException.class, () -> userService.deleteUser(userId, reqDto));
    }

    @Test
    @Order(12)
    void 비밀번호_수정_성공() {

        // given
        Long userId = createAndGetUserId("testUsername");

        UserPasswordChangeReqDto reqDto = UserPasswordChangeReqDto.builder()
                .password("password1")
                .newPassword("newPassword1")
                .checkNewPassword("newPassword1")
                .build();

        // when
        userService.changePassword(userId, reqDto);
        User user = userRepository.findById(userId).orElseThrow();

        // then
        assertTrue(passwordEncoder.matches(reqDto.getNewPassword(), user.getPassword()));
    }

    @Test
    @Order(13)
    void 비밀번호_수정_실패_현재_비밀번호_불일치() {

        // given
        Long userId = createAndGetUserId("testUsername");

        UserPasswordChangeReqDto reqDto = UserPasswordChangeReqDto.builder()
                .password("wrongPassword")
                .newPassword("newPassword1")
                .checkNewPassword("newPassword1")
                .build();

        // when & then
        assertThrows(UserException.class, () -> userService.changePassword(userId, reqDto));
    }

    @Test
    @Order(14)
    void 비밀번호_수정_실패_비밀번호_확인_불일치() {

        // given
        Long userId = createAndGetUserId("testUsername");

        UserPasswordChangeReqDto reqDto = UserPasswordChangeReqDto.builder()
                .password("password1")
                .newPassword("newPassword1")
                .checkNewPassword("newPassword2")
                .build();

        // when & then
        assertThrows(UserException.class, () -> userService.changePassword(userId, reqDto));
    }

    @Test
    @Order(15)
    void 비밀번호_수정_실패_기존_비밀번호와_일치() {

        // given
        Long userId = createAndGetUserId("testUsername");

        UserPasswordChangeReqDto reqDto = UserPasswordChangeReqDto.builder()
                .password("password1")
                .newPassword("password1")
                .checkNewPassword("password1")
                .build();

        // when & then
        assertThrows(UserException.class, () -> userService.changePassword(userId, reqDto));
    }



}

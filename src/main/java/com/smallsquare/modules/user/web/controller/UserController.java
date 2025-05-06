package com.smallsquare.modules.user.web.controller;

import com.smallsquare.modules.user.application.service.UserService;
import com.smallsquare.modules.user.infrastructure.security.model.CustomUserDetails;
import com.smallsquare.modules.user.web.dto.request.*;
import com.smallsquare.modules.user.web.dto.response.UserInfoResDto;
import com.smallsquare.modules.user.web.dto.response.UserLoginResDto;
import com.smallsquare.modules.user.web.dto.response.UserUpdateResDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    /**
     * 회원가입
     * @param reqDto : username, password, nickname, email, name
     * @return 201 Created
     */
    @PostMapping("/")
    public ResponseEntity<Void> signup(@Valid @RequestBody UserSignupReqDto reqDto){
        userService.signup(reqDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 로그인
     * @param reqDto : username, password
     * @return 200 Success / resDto : accessToken, refreshToken
     */
    @PostMapping("/login")
    public ResponseEntity<UserLoginResDto> login(@RequestBody UserLoginReqDto reqDto) {
        UserLoginResDto resDto = userService.login(reqDto);
        return ResponseEntity.status(HttpStatus.OK).body(resDto);
    }

    /**
     * 로그아웃
     * @param reqDto : accessToken, refreshToken
     * @return 200 Success
     */

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody UserLogoutReqDto reqDto) {
        userService.logout(reqDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 내 정보 조회
     * @return 200 Success / UserInfoResDto (username, name, email, nickname)
     */
    @GetMapping("/me")
    public ResponseEntity<UserInfoResDto> me(@AuthenticationPrincipal CustomUserDetails userDetails) {
        UserInfoResDto resDto = userService.me(userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.OK).body(resDto);
    }

    /**
     * 회원정보 수정
     * @param userDetails
     * @param reqDto
     * @return
     */
    @PatchMapping("/me")
    public ResponseEntity<UserUpdateResDto> updateMyInfo(@Valid @AuthenticationPrincipal CustomUserDetails userDetails,
                                                         @RequestBody UserUpdateReqDto reqDto) {
        UserUpdateResDto resDto = userService.updateUserInfo(userDetails.getUserId(), reqDto);
        return ResponseEntity.status(HttpStatus.OK).body(resDto);
    }

    @DeleteMapping("me")
    public ResponseEntity<Void> deleteMyInfo(@AuthenticationPrincipal CustomUserDetails userDetails,
                                             @RequestBody UserDeleteReqDto reqDto) {
        userService.deleteUser(userDetails.getUserId(), reqDto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}

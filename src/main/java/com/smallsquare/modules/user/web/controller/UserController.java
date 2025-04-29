package com.smallsquare.modules.user.web.controller;

import com.smallsquare.common.response.Response;
import com.smallsquare.modules.user.application.service.UserService;
import com.smallsquare.modules.user.web.dto.request.UserLoginReqDto;
import com.smallsquare.modules.user.web.dto.request.UserSignupReqDto;
import com.smallsquare.modules.user.web.dto.response.UserLoginResDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
     * @return resDto : accessToken, refreshToken
     */
    @PostMapping("/login")
    public ResponseEntity<UserLoginResDto> login(@RequestBody UserLoginReqDto reqDto) {
        UserLoginResDto resDto = userService.login(reqDto);
        return ResponseEntity.status(HttpStatus.OK).body(resDto);
    }
}

package com.smallsquare.user.web.controller;

import com.smallsquare.user.application.service.UserService;
import com.smallsquare.user.web.dto.request.UserSignupReqDto;
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
     * @return void
     */
    @PostMapping("/")
    public ResponseEntity<Void> signup(@Valid @RequestBody UserSignupReqDto reqDto){
        userService.signup(reqDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}

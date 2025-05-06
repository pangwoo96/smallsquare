package com.smallsquare.modules.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smallsquare.modules.user.application.service.UserService;
import com.smallsquare.modules.user.web.controller.UserController;
import com.smallsquare.modules.user.web.dto.request.UserSignupReqDto;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)  // Security 필터 비활성화 (필요하면)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserPasswordChangeReqDtoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @Order(1)
    void 회원가입_요청_성공() throws Exception {
        // given
        UserSignupReqDto reqDto = UserSignupReqDto.builder()
                .username("username1")
                .password("password1")
                .checkPassword("password1")
                .nickname("nickname1")
                .email("email1@gmail.com")
                .name("name1")
                .build();

        // when & then
        mockMvc.perform(post("/users/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqDto)))
                .andExpect(status().isCreated());
    }
}
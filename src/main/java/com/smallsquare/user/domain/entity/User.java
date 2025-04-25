package com.smallsquare.user.domain.entity;

import com.smallsquare.common.util.BaseTimeEntity;
import com.smallsquare.user.domain.enums.Role;
import com.smallsquare.user.web.dto.request.UserSignupReqDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity @Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String username;

    private String password;

    private String nickname;

    private String email;

    private String name;

    @Enumerated(EnumType.STRING)
    private Role role;

    public static User of(UserSignupReqDto reqDto, String password) {
        return User.builder()
                .username(reqDto.getUsername())
                .password(password)
                .nickname(reqDto.getNickname())
                .email(reqDto.getEmail())
                .name(reqDto.getName())
                .role(reqDto.getRole())
                .build();
    }

}

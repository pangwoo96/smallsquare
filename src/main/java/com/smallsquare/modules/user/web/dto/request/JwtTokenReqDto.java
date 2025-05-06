package com.smallsquare.modules.user.web.dto.request;

import com.smallsquare.modules.user.domain.enums.Role;
import lombok.*;

@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtTokenReqDto {

    private Long userId;

    private String username;

    private String password;

    private String nickname;

    private String email;

    private String name;

    private Role role = Role.USER;
}

package com.smallsquare.modules.user.web.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateResDto {

    private String username;

    private String name;

    private String email;

    private String nickname;
}

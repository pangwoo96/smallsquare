package com.smallsquare.modules.user.web.dto.response;

import lombok.*;

@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResDto {

    private String username;

    private String nickname;

    private String name;

    private String email;
}

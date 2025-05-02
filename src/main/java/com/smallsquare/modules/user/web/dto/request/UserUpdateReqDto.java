package com.smallsquare.modules.user.web.dto.request;

import lombok.*;

@Getter @Setter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateReqDto {

    private String username;

    private String name;

    private String email;

    private String nickname;
}

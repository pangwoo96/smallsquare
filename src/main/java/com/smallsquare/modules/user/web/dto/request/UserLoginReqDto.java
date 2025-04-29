package com.smallsquare.modules.user.web.dto.request;

import lombok.*;

@Getter @Setter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginReqDto {

    private String username;

    private String password;
}

package com.smallsquare.modules.user.web.dto.request;

import lombok.*;

@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDeleteReqDto {

    private String password;
}

package com.smallsquare.modules.user.web.dto.request;

import lombok.*;

@Getter @Setter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDeleteReqDto {

    private String password;
}

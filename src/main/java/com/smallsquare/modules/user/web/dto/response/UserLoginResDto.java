package com.smallsquare.modules.user.web.dto.response;

import lombok.*;

@Getter @Setter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginResDto {

    private String accessToken;

    private String refreshToken;
}

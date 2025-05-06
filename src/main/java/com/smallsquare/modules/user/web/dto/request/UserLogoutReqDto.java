package com.smallsquare.modules.user.web.dto.request;

import lombok.*;

@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLogoutReqDto {

    private String accessToken;

    private String refreshToken;
}

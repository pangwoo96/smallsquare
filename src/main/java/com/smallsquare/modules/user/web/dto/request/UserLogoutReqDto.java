package com.smallsquare.modules.user.web.dto.request;

import lombok.*;

@Getter @Setter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLogoutReqDto {

    private String accessToken;

    private String refreshToken;
}

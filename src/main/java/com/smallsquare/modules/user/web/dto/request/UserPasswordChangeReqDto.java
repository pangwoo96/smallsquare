package com.smallsquare.modules.user.web.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPasswordChangeReqDto {

    private String password;

    private String newPassword;

    private String checkNewPassword;
}

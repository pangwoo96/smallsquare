package com.smallsquare.modules.user.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVerifyEmailReqDto {

    @NotBlank(message = "Token은 필수값입니다.")
    private String verifyEmailToken;
}

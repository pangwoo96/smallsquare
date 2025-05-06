package com.smallsquare.modules.user.web.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateReqDto {

    @NotNull
    private String username;

    @NotNull
    private String name;

    @NotNull
    private String email;

    @NotNull
    private String nickname;
}

package com.ssafy12.moinsoop.skinfit.domain.user.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignUpRequest {
    private String userEmail;
    private String userPassword;
}

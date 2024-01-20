package com.poorlex.poorlex.auth.service.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LoginRequest {

    private final String oauthId;
    private final String nickname;
}

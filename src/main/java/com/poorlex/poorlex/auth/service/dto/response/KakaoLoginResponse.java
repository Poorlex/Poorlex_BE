package com.poorlex.poorlex.auth.service.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoLoginResponse(String id, @JsonProperty("kakao_account") KakaoAccount kakaoAccount) {

    public record KakaoAccount(String email, Profile profile) {}

    public record Profile(String nickname) {}
}

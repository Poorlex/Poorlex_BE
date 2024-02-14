package com.poorlex.poorlex.member.domain;

public enum Oauth2RegistrationId {
    KAKAO, APPLE;

    public static Oauth2RegistrationId findByName(final String name) {
        return valueOf(name.toUpperCase());
    }
}

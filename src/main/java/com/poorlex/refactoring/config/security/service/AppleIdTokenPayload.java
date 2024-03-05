package com.poorlex.refactoring.config.security.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AppleIdTokenPayload {

    private final String sub;
    private final String email;
}

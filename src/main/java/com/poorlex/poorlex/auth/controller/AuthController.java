package com.poorlex.poorlex.auth.controller;

import com.poorlex.poorlex.auth.service.AuthService;
import com.poorlex.poorlex.auth.service.authenticator.AppleAuthenticator;
import com.poorlex.poorlex.auth.service.authenticator.Authenticator;
import com.poorlex.poorlex.auth.service.authenticator.KakaoAuthenticator;
import com.poorlex.poorlex.auth.service.dto.request.LoginRequest;
import com.poorlex.poorlex.auth.service.Oauth2Provider;
import com.poorlex.poorlex.auth.service.dto.response.LoginTokenResponse;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("login")
@RequiredArgsConstructor
@Hidden
public class AuthController {

    private final AuthService authService;
    private final KakaoAuthenticator kakaoAuthenticator;
    private final AppleAuthenticator appleAuthenticator;
    private final Map<Oauth2Provider, Authenticator> authenticators = new HashMap<>();

    @PostConstruct
    private void postConstruct() {
        authenticators.putIfAbsent(Oauth2Provider.kakao, kakaoAuthenticator);
        authenticators.putIfAbsent(Oauth2Provider.apple, appleAuthenticator);
    }

    /**
     * @deprecated 시큐리티 도입으로 인해 사용하지 않는 API
     */
    @Deprecated(forRemoval = true)
    @PostMapping
    public ResponseEntity<LoginTokenResponse> loginAfterRegisterIfNotExist(@RequestBody LoginRequest request) {
        final LoginTokenResponse response = authService.loginAfterRegisterIfNotExist(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/success")
    public ResponseEntity<String> login(@RequestParam(name = "accessToken") final String accessToken) {
        return ResponseEntity.ok(accessToken);
    }

    @PostMapping("/oauth/{provider}")
    public ResponseEntity<?> oauthLogin(@PathVariable Oauth2Provider provider, final String code) {
        LoginTokenResponse loginTokenResponse = authService.oauthLoginAfterRegisterIfNotExist(authenticators.get(provider), code);

        return ResponseEntity.ok(loginTokenResponse);
    }
}

package com.poorlex.poorlex.auth.controller;

import com.poorlex.poorlex.auth.service.AuthService;
import com.poorlex.poorlex.auth.service.dto.request.LoginRequest;
import com.poorlex.poorlex.auth.service.dto.request.Oauth2Provider;
import com.poorlex.poorlex.auth.service.dto.response.LoginTokenResponse;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("login")
@RequiredArgsConstructor
@Hidden
public class AuthController {

    private final AuthService authService;

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

    @PostMapping("/{oauth2Provider}/exchange")
    public ResponseEntity<LoginTokenResponse> exchangeToken(@PathVariable Oauth2Provider oauth2Provider, final String accessToken) {
        LoginTokenResponse loginTokenResponse = authService.exchangeTokenAfterRegisterIfNotExist(oauth2Provider, accessToken);

        return ResponseEntity.ok(loginTokenResponse);
    }
}

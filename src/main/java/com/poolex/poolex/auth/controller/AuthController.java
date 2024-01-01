package com.poolex.poolex.auth.controller;

import com.poolex.poolex.auth.service.AuthService;
import com.poolex.poolex.auth.service.dto.request.LoginRequest;
import com.poolex.poolex.auth.service.dto.response.LoginTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("login")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping
    public ResponseEntity<LoginTokenResponse> loginAfterRegisterIfNotExist(@RequestBody LoginRequest request) {
        final LoginTokenResponse response = authService.loginAfterRegisterIfNotExist(request);

        return ResponseEntity.ok(response);
    }
}

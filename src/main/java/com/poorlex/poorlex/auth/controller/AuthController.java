package com.poorlex.poorlex.auth.controller;

import com.poorlex.poorlex.auth.service.AuthService;
import com.poorlex.poorlex.auth.service.dto.request.LoginRequest;
import com.poorlex.poorlex.auth.service.dto.response.LoginTokenResponse;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}

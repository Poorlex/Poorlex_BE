package com.poorlex.poorlex.auth.service.authenticator;

import com.poorlex.poorlex.auth.service.JwtTokenProvider;
import com.poorlex.poorlex.auth.service.dto.response.AppleIdTokenPayload;
import com.poorlex.poorlex.auth.service.dto.response.AppleLoginResponse;
import com.poorlex.poorlex.user.member.domain.Oauth2RegistrationId;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppleAuthenticator implements Authenticator {

    @Value("${apple.client-id}")
    String clientId;

    @Value("${apple.private-key}")
    String clientSecret;

    private final RestClient restClient = RestClient.create();
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public String authenticate(String code) {
        MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
        request.put("code", List.of(code));
        request.put("client_id", List.of(clientId));
        request.put("client_secret", List.of(clientSecret));
        request.put("grant_type", List.of("authorization_code"));

        AppleLoginResponse response = restClient.post()
                .uri("https://appleid.apple.com/auth/token")
                .body(request)
                .retrieve()
                .body(AppleLoginResponse.class);

        assert response != null;
        AppleIdTokenPayload appleIdTokenPayload = jwtTokenProvider.decodePayload(response.idToken(), AppleIdTokenPayload.class);

        return appleIdTokenPayload.sub();
    }

    @Override
    public Oauth2RegistrationId provider() {
        return Oauth2RegistrationId.APPLE;
    }
}

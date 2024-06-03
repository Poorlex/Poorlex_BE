package com.poorlex.poorlex.auth.service.authenticator;

import com.poorlex.poorlex.auth.service.dto.response.KakaoLoginResponse;
import com.poorlex.poorlex.user.member.domain.Oauth2RegistrationId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class KakaoAuthenticator implements Authenticator {

    private final RestClient restClient = RestClient.create();

    @Override
    public String authenticate(final String token) {
        KakaoLoginResponse response = restClient.post()
                .uri("https://kapi.kakao.com/v2/user/me")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(KakaoLoginResponse.class);

        assert response != null;
        return response.id();
    }

    @Override
    public Oauth2RegistrationId provider() {
        return Oauth2RegistrationId.KAKAO;
    }
}

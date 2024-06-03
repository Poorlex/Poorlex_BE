package com.poorlex.poorlex.user.member.service;

import com.poorlex.poorlex.user.member.service.dto.response.NicknameGenerateResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class NicknameGenerator {

    private final RestClient restClient = RestClient.create();

    public String generate() {
        NicknameGenerateResponse response = restClient.post()
                .uri("https://www.rivestsoft.com/nickname/getRandomNickname.ajax")
                .retrieve()
                .body(NicknameGenerateResponse.class);

        assert response != null;
        return response.nickname();
    }
}

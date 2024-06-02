package com.poorlex.poorlex.auth.service.dto.request;

import com.poorlex.poorlex.auth.service.dto.response.KakaoLoginResponse;
import com.poorlex.poorlex.user.member.domain.Member;
import com.poorlex.poorlex.user.member.domain.MemberNickname;
import com.poorlex.poorlex.user.member.domain.Oauth2RegistrationId;
import org.springframework.web.client.RestClient;

public enum Oauth2Provider {
    kakao {
        @Override
        public Member userInfo(String accessToken) {
            KakaoLoginResponse response = restClient.post()
                    .uri("https://kapi.kakao.com/v2/user/me")
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .body(KakaoLoginResponse.class);

            assert response != null;
            MemberNickname memberNickname = new MemberNickname(response.kakaoAccount().profile().nickname());
            return Member.withoutId(Oauth2RegistrationId.KAKAO, response.id(), memberNickname);
        }
    };

    final RestClient restClient = RestClient.create();

    public abstract Member userInfo(String accessToken);
}

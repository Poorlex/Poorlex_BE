package com.poorlex.poorlex.auth.service;

import com.poorlex.poorlex.auth.service.dto.request.LoginRequest;
import com.poorlex.poorlex.auth.service.dto.request.Oauth2Provider;
import com.poorlex.poorlex.auth.service.dto.response.LoginTokenResponse;
import com.poorlex.poorlex.user.member.domain.Member;
import com.poorlex.poorlex.user.member.domain.MemberNickname;
import com.poorlex.poorlex.user.member.domain.MemberRepository;
import com.poorlex.poorlex.user.member.domain.Oauth2RegistrationId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public LoginTokenResponse loginAfterRegisterIfNotExist(final LoginRequest request) {
        final Member member = memberRepository.findByOauthId(request.getOauthId())
                .orElseGet(() -> createMember(request));
        final String accessToken = jwtTokenProvider.createAccessToken(member.getId());

        return new LoginTokenResponse(accessToken);
    }

    private Member createMember(final LoginRequest request) {
        return memberRepository.save(
                Member.withoutId(Oauth2RegistrationId.APPLE, request.getOauthId(),
                                 new MemberNickname(request.getNickname()))
        );
    }

    @Transactional
    public LoginTokenResponse exchangeTokenAfterRegisterIfNotExist(final Oauth2Provider oauth2Provider, final String oauth2AccessToken) {
        Member userInfo = userInfo(oauth2Provider, oauth2AccessToken);
        Member member = memberRepository.findByOauthId(userInfo.getOauthId())
                .orElseGet(() -> memberRepository.save(userInfo));

        final String accessToken = jwtTokenProvider.createAccessToken(member.getId());

        return new LoginTokenResponse(accessToken);
    }

    private Member userInfo(final Oauth2Provider oauth2Provider, final String accessToken) {
        return oauth2Provider.userInfo(accessToken);
    }
}

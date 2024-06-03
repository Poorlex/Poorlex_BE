package com.poorlex.poorlex.auth.service;

import com.poorlex.poorlex.auth.service.authenticator.Authenticator;
import com.poorlex.poorlex.auth.service.dto.request.LoginRequest;
import com.poorlex.poorlex.auth.service.dto.response.LoginTokenResponse;
import com.poorlex.poorlex.user.member.domain.Member;
import com.poorlex.poorlex.user.member.domain.MemberNickname;
import com.poorlex.poorlex.user.member.domain.MemberRepository;
import com.poorlex.poorlex.user.member.domain.Oauth2RegistrationId;
import com.poorlex.poorlex.user.member.service.NicknameGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final NicknameGenerator nicknameGenerator;

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
    public LoginTokenResponse oauthLoginAfterRegisterIfNotExist(Authenticator authenticator, final String code) {
        String oauthId = authenticator.authenticate(code);
        Member member = memberRepository.findByOauthId(oauthId)
                .orElseGet(() -> {
                    MemberNickname nickname = new MemberNickname(nicknameGenerator.generate());
                    return memberRepository.save(Member.withoutId(authenticator.provider(), oauthId, nickname));
                });

        final String accessToken = jwtTokenProvider.createAccessToken(member.getId());

        return new LoginTokenResponse(accessToken);
    }
}

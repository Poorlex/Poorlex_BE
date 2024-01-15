package com.poolex.poolex.auth.service;

import com.poolex.poolex.auth.service.dto.request.LoginRequest;
import com.poolex.poolex.auth.service.dto.response.LoginTokenResponse;
import com.poolex.poolex.member.domain.Member;
import com.poolex.poolex.member.domain.MemberNickname;
import com.poolex.poolex.member.domain.MemberRepository;
import com.poolex.poolex.token.JwtTokenProvider;
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
            Member.withoutId(request.getOauthId(), new MemberNickname(request.getNickname()))
        );
    }
}

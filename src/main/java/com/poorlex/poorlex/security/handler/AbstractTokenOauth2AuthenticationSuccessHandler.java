package com.poorlex.poorlex.security.handler;

import com.poorlex.poorlex.member.domain.MemberRepository;
import com.poorlex.poorlex.token.JwtTokenProvider;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;

public abstract class AbstractTokenOauth2AuthenticationSuccessHandler extends
    AbstractAuthenticationTargetUrlRequestHandler
    implements Oauth2AuthenticationSuccessHandler {

    protected static final int NICKNAME_MINIMUM_LENGTH = 2;
    protected static final int NICKNAME_MAXIMUM_LENGTH = 15;
    protected static final String SHORT_NICKNAME_PREFIX = "[Poorlex]";

    protected final MemberRepository memberRepository;
    protected final JwtTokenProvider jwtTokenProvider;

    protected AbstractTokenOauth2AuthenticationSuccessHandler(final MemberRepository memberRepository,
                                                              final JwtTokenProvider jwtTokenProvider) {
        this.memberRepository = memberRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public boolean supports(final String registrationId) {
        return false;
    }

    protected String createToken(final Long memberId) {
        return jwtTokenProvider.createAccessToken(memberId);
    }
}

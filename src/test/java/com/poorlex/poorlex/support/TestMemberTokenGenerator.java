package com.poorlex.poorlex.support;

import com.poorlex.poorlex.member.domain.Member;
import com.poorlex.poorlex.member.domain.MemberNickname;
import com.poorlex.poorlex.member.domain.MemberRepository;
import com.poorlex.poorlex.member.domain.Oauth2RegistrationId;
import com.poorlex.poorlex.token.JwtTokenProvider;

public class TestMemberTokenGenerator {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public TestMemberTokenGenerator(final MemberRepository memberRepository, final JwtTokenProvider jwtTokenProvider) {
        this.memberRepository = memberRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public String createTokenWithNewMember(final String oauthId) {
        final Member member = createMember(oauthId);
        return createAccessToken(member);
    }

    private Member createMember(final String oauthId) {
        final Member member = Member.withoutId(Oauth2RegistrationId.APPLE, oauthId, new MemberNickname("nickname"));
        return memberRepository.save(member);
    }

    public String createAccessToken(final Member member) {
        return jwtTokenProvider.createAccessToken(member.getId());
    }
}

package com.poolex.poolex.support;

import com.poolex.poolex.member.domain.Member;
import com.poolex.poolex.member.domain.MemberNickname;
import com.poolex.poolex.member.domain.MemberRepository;
import com.poolex.poolex.token.JwtTokenProvider;

public class TestMemberTokenGenerator {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public TestMemberTokenGenerator(final MemberRepository memberRepository, final JwtTokenProvider jwtTokenProvider) {
        this.memberRepository = memberRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public String createTokenWithNewMember(final String nickname) {
        final Member member = createMember(nickname);
        return createAccessToken(member);
    }

    private Member createMember(final String nickname) {
        final Member member = Member.withoutId(new MemberNickname(nickname));
        return memberRepository.save(member);
    }

    public String createAccessToken(final Member member) {
        return jwtTokenProvider.createAccessToken(member.getId());
    }
}

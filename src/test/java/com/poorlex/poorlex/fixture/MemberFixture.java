package com.poorlex.poorlex.fixture;

import com.poorlex.poorlex.user.member.domain.Member;
import com.poorlex.poorlex.user.member.domain.MemberDescription;
import com.poorlex.poorlex.user.member.domain.MemberNickname;
import com.poorlex.poorlex.user.member.domain.MemberRepository;
import com.poorlex.poorlex.user.member.domain.Oauth2RegistrationId;

public class MemberFixture {

    private MemberFixture() {

    }

    public static Member saveMemberWithOauthId(final MemberRepository memberRepository,
                                               final Oauth2RegistrationId oauth2RegistrationId,
                                               final String oauthId,
                                               final String nickname,
                                               final String description) {
        final Member member = Member.withoutId(oauth2RegistrationId, oauthId, new MemberNickname(nickname));
        member.changeDescription(new MemberDescription(description));
        return memberRepository.save(member);
    }
}

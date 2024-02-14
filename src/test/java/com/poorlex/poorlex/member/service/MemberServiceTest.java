package com.poorlex.poorlex.member.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.poorlex.poorlex.member.domain.Member;
import com.poorlex.poorlex.member.domain.MemberDescription;
import com.poorlex.poorlex.member.domain.MemberNickname;
import com.poorlex.poorlex.member.domain.MemberRepository;
import com.poorlex.poorlex.member.domain.Oauth2RegistrationId;
import com.poorlex.poorlex.member.service.dto.request.MemberProfileUpdateRequest;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import com.poorlex.poorlex.support.db.UsingDataJpaTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MemberServiceTest extends UsingDataJpaTest implements ReplaceUnderScoreTest {

    @Autowired
    private MemberRepository memberRepository;
    private MemberService memberService;

    @BeforeEach
    void setUp() {
        this.memberService = new MemberService(memberRepository);
    }

    @Test
    void 멤버의_프로필을_업데이트한다() {
        //given
        final Member member = memberRepository.save(
            Member.withoutId(Oauth2RegistrationId.APPLE, "oauthId", new MemberNickname("nickname")));
        final MemberProfileUpdateRequest request = new MemberProfileUpdateRequest("newNickname", "newDescription");

        //when
        memberService.updateProfile(member.getId(), request);

        //then
        final Member updatedMember = memberRepository.findById(member.getId()).get();
        assertThat(updatedMember.getNickname()).isEqualTo(request.getNickname());
        assertThat(updatedMember.getDescription()).isPresent()
            .get()
            .isEqualTo(request.getDescription());
    }

    @Test
    void 멤버의_프로필을_업데이트한다_닉네임이_null일_경우() {
        //given
        final Member prevMember = Member.withoutId(Oauth2RegistrationId.APPLE, "oauthId",
            new MemberNickname("nickname"));
        prevMember.changeDescription(new MemberDescription("description"));
        memberRepository.save(prevMember);
        final MemberProfileUpdateRequest request = new MemberProfileUpdateRequest(null, "newDescription");

        //when
        memberService.updateProfile(prevMember.getId(), request);

        //then
        final Member updatedMember = memberRepository.findById(prevMember.getId()).get();
        assertThat(updatedMember.getNickname()).isEqualTo("nickname");
        assertThat(updatedMember.getDescription()).isPresent()
            .get()
            .isEqualTo(request.getDescription());
    }

    @Test
    void 멤버의_프로필을_업데이트한다_소개가_null일_경우() {
        //given
        final Member prevMember = Member.withoutId(Oauth2RegistrationId.APPLE, "oauthId",
            new MemberNickname("nickname"));
        prevMember.changeDescription(new MemberDescription("description"));
        memberRepository.save(prevMember);
        final MemberProfileUpdateRequest request = new MemberProfileUpdateRequest("newNickname", null);

        //when
        memberService.updateProfile(prevMember.getId(), request);

        //then
        final Member updatedMember = memberRepository.findById(prevMember.getId()).get();
        assertThat(updatedMember.getNickname()).isEqualTo(request.getNickname());
        assertThat(updatedMember.getDescription()).isPresent()
            .get()
            .isEqualTo("description");
    }

    @Test
    void 멤버의_프로필을_업데이트한다_둘다_null일_경우() {
        //given
        final Member prevMember = Member.withoutId(Oauth2RegistrationId.APPLE, "oauthId",
            new MemberNickname("nickname"));
        prevMember.changeDescription(new MemberDescription("description"));
        memberRepository.save(prevMember);
        final MemberProfileUpdateRequest request = new MemberProfileUpdateRequest(null, null);

        //when
        memberService.updateProfile(prevMember.getId(), request);

        //then
        final Member updatedMember = memberRepository.findById(prevMember.getId()).get();
        assertThat(updatedMember.getNickname()).isEqualTo("nickname");
        assertThat(updatedMember.getDescription()).isPresent()
            .get()
            .isEqualTo("description");
    }
}

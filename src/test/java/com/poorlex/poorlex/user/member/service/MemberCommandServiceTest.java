package com.poorlex.poorlex.user.member.service;

import com.poorlex.poorlex.fixture.MemberFixture;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import com.poorlex.poorlex.support.db.UsingDataJpaTest;
import com.poorlex.poorlex.user.member.domain.Member;
import com.poorlex.poorlex.user.member.domain.MemberRepository;
import static com.poorlex.poorlex.user.member.domain.Oauth2RegistrationId.APPLE;
import com.poorlex.poorlex.user.member.service.dto.request.MemberProfileUpdateRequest;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


@DisplayName("회원 정보 관리 테스트")
class MemberCommandServiceTest extends UsingDataJpaTest implements ReplaceUnderScoreTest {

    @Autowired
    private MemberRepository memberRepository;

    private MemberCommandService memberService;

    @BeforeEach
    void setUp() {
        this.memberService = new MemberCommandService(memberRepository);
    }

    @Test
    void 멤버의_프로필을_업데이트한다() {
        //given
        final String 기존_닉네임 = "기존 닉네임";
        final String 수정_닉네임 = "수정될 닉네임";

        final String 기존_소개 = "기존 소개";
        final String 수정_소개 = "수정될 소개";

        final Member 회원 = MemberFixture.saveMemberWithOauthId(memberRepository, APPLE, "oauthId", 기존_닉네임, 기존_소개);
        final MemberProfileUpdateRequest 수정_요청 = new MemberProfileUpdateRequest(수정_닉네임, 수정_소개);

        //when
        memberService.updateProfile(회원.getId(), 수정_요청);
        영속성_컨텍스트를_플러시하고_초기화한다();

        //then
        final Member 수정된_회원 = memberRepository.findById(회원.getId()).get();
        final String 수정_후_닉네임 = 수정된_회원.getNickname();
        final Optional<String> 수정_후_소개 = 수정된_회원.getDescription();

        assertThat(수정_후_닉네임).isEqualTo(수정_닉네임);
        assertThat(수정_후_소개).contains(수정_소개);
    }

    @Test
    void 업데이트시_닉네임이_null이면_닉네임은_업데이트되지_않는다() {
        //given
        final String 기존_닉네임 = "닉네임";

        final String 수정_닉네임 = null;
        final String 수정_소개 = "수정 소개";

        final Member 회원 = MemberFixture.saveMemberWithOauthId(memberRepository, APPLE, "oauthId", 기존_닉네임, "소개");
        final MemberProfileUpdateRequest 수정_요청 = new MemberProfileUpdateRequest(수정_닉네임, 수정_소개);

        //when
        memberService.updateProfile(회원.getId(), 수정_요청);
        영속성_컨텍스트를_플러시하고_초기화한다();

        //then
        final Member 수정된_회원 = memberRepository.findById(회원.getId()).get();
        final String 수정_후_닉네임 = 수정된_회원.getNickname();
        final Optional<String> 수정_후_소개 = 수정된_회원.getDescription();

        assertThat(수정_후_닉네임).isEqualTo(기존_닉네임);
        assertThat(수정_후_소개).contains(수정_소개);
    }

    @Test
    void 업데이트시_회원소개가_null이면_회원소개는_업데이트되지_않는다() {
        //given
        final String 기존_소개 = "소개";

        final String 수정_소개 = null;
        final String 수정_닉네임 = "수정 닉네임";

        final Member 회원 = MemberFixture.saveMemberWithOauthId(memberRepository, APPLE, "oauthId", "닉네임", 기존_소개);
        final MemberProfileUpdateRequest 수정_요청 = new MemberProfileUpdateRequest(수정_닉네임, 수정_소개);

        //when
        memberService.updateProfile(회원.getId(), 수정_요청);
        영속성_컨텍스트를_플러시하고_초기화한다();

        //then
        final Member 수정된_회원 = memberRepository.findById(회원.getId()).get();
        final String 수정_후_닉네임 = 수정된_회원.getNickname();
        final Optional<String> 수정_후_소개 = 수정된_회원.getDescription();

        assertThat(수정_후_닉네임).isEqualTo(수정_닉네임);
        assertThat(수정_후_소개).contains(기존_소개);
    }

    @Test
    void 업데이트시_회원소개와_닉네임이_모두_null이면_모두_업데이트되지_않는다() {
        //given
        final String 기존_닉네임 = "닉네임";
        final String 기존_소개 = "소개";

        final String 수정_닉네임 = null;
        final String 수정_소개 = null;

        final Member 회원 = MemberFixture.saveMemberWithOauthId(memberRepository, APPLE, "oauthId", 기존_닉네임, 기존_소개);
        final MemberProfileUpdateRequest 수정_요청 = new MemberProfileUpdateRequest(수정_닉네임, 수정_소개);

        //when
        memberService.updateProfile(회원.getId(), 수정_요청);
        영속성_컨텍스트를_플러시하고_초기화한다();

        //then
        final Member 수정된_회원 = memberRepository.findById(회원.getId()).get();
        final String 수정_후_닉네임 = 수정된_회원.getNickname();
        final Optional<String> 수정_후_소개 = 수정된_회원.getDescription();

        assertThat(수정_후_닉네임).isEqualTo(기존_닉네임);
        assertThat(수정_후_소개).contains(기존_소개);
    }
}

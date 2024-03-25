package com.poorlex.poorlex.user.point.service;

import com.poorlex.poorlex.fixture.MemberFixture;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import com.poorlex.poorlex.support.db.UsingDataJpaTest;
import com.poorlex.poorlex.user.member.domain.Member;
import com.poorlex.poorlex.user.member.domain.MemberRepository;
import static com.poorlex.poorlex.user.member.domain.Oauth2RegistrationId.APPLE;
import com.poorlex.poorlex.user.point.domain.MemberPoint;
import com.poorlex.poorlex.user.point.domain.MemberPointRepository;
import java.util.List;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MemberPointCommandServiceTest extends UsingDataJpaTest implements ReplaceUnderScoreTest {

    @Autowired
    private MemberPointRepository memberPointRepository;

    @Autowired
    private MemberRepository memberRepository;

    private MemberPointCommandService memberPointCommandService;

    @BeforeEach
    void setUp() {
        this.memberPointCommandService = new MemberPointCommandService(memberPointRepository, memberRepository);
    }

    @Test
    void 멤버_포인트를_생성한다() {
        //given
        final Member 스플릿 = MemberFixture.saveMemberWithOauthId(memberRepository, APPLE, "고유 ID", "스플릿", "소개");
        final int 지급_포인트_양 = 10;

        //when
        memberPointCommandService.createPoint(스플릿.getId(), 지급_포인트_양);
        영속성_컨텍스트를_플러시하고_초기화한다();

        //then
        final List<MemberPoint> 회원_포인트_목록 = memberPointRepository.findAll();
        assertSoftly(
                softly -> {
                    softly.assertThat(회원_포인트_목록).hasSize(1);
                    final MemberPoint 회원_포인트 = 회원_포인트_목록.get(0);
                    softly.assertThat(회원_포인트.getPoint()).isEqualTo(지급_포인트_양);
                    softly.assertThat(회원_포인트.getMemberId()).isEqualTo(스플릿.getId());
                }
        );
    }
}

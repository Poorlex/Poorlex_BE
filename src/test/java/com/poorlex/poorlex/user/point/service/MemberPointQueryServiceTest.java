package com.poorlex.poorlex.user.point.service;

import com.poorlex.poorlex.fixture.MemberFixture;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import com.poorlex.poorlex.support.db.UsingDataJpaTest;
import com.poorlex.poorlex.user.member.domain.Member;
import com.poorlex.poorlex.user.member.domain.MemberLevel;
import com.poorlex.poorlex.user.member.domain.MemberRepository;
import static com.poorlex.poorlex.user.member.domain.Oauth2RegistrationId.APPLE;
import com.poorlex.poorlex.user.point.domain.MemberPoint;
import com.poorlex.poorlex.user.point.domain.MemberPointRepository;
import com.poorlex.poorlex.user.point.domain.Point;
import com.poorlex.poorlex.user.point.service.dto.response.MemberLevelBarResponse;
import com.poorlex.poorlex.user.point.service.dto.response.MemberPointAndLevelResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("회원 포인트 조회 서비스 테스트")
class MemberPointQueryServiceTest extends UsingDataJpaTest implements ReplaceUnderScoreTest {

    @Autowired
    private MemberPointRepository memberPointRepository;

    @Autowired
    private MemberRepository memberRepository;

    private MemberPointQueryService memberPointQueryService;

    @BeforeEach
    void setUp() {
        this.memberPointQueryService = new MemberPointQueryService(memberPointRepository);
        initializeDataBase();
    }

    @Test
    void 멤버_포인트의_총합을_조회한다() {
        //given
        final Member 스플릿 = MemberFixture.saveMemberWithOauthId(memberRepository, APPLE, "고유 ID", "스플릿", "소개");
        final int 지급할_포인트_양 = 10;
        포인트를_지급한다(스플릿, 지급할_포인트_양);

        //when
        final MemberPointAndLevelResponse 회원_총포인트와_레벨_응답값 = memberPointQueryService.findMemberSumPointAndLevel(스플릿.getId());

        //then
        final MemberLevel 회원_레벨 = MemberLevel.findByPoint(new Point(지급할_포인트_양)).get();
        assertThat(회원_총포인트와_레벨_응답값.getTotalPoint()).isEqualTo(지급할_포인트_양);
        assertThat(회원_총포인트와_레벨_응답값.getLevel()).isEqualTo(회원_레벨.getNumber());
    }

    @Test
    void 멤버_레벨바에_필요한_정보를_조회한다() {
        //given
        final Member 스플릿 = MemberFixture.saveMemberWithOauthId(memberRepository, APPLE, "고유 ID", "스플릿", "소개");
        final int 지급할_포인트_양 = 10;
        포인트를_지급한다(스플릿, 지급할_포인트_양);

        //when
        final MemberLevelBarResponse 회원_레벨바_응답값 = memberPointQueryService.findMemberLevelBarInfo(스플릿.getId());

        //then
        final int 예상_회원_레벨_구간_길이 = MemberLevel.findByPoint(new Point(지급할_포인트_양)).get().getLevelRange();

        assertSoftly(
                softly -> {
                    softly.assertThat(회원_레벨바_응답값.getLevelRange()).isEqualTo(예상_회원_레벨_구간_길이);
                    softly.assertThat(회원_레벨바_응답값.getCurrentPoint()).isEqualTo(지급할_포인트_양);
                    softly.assertThat(회원_레벨바_응답값.getRecentPoint()).isEqualTo(지급할_포인트_양);
                }
        );
    }

    @Test
    void 멤버_레벨바에_필요한_정보를_조회한다_포인트가_없을_때() {
        //given
        final Member 스플릿 = MemberFixture.saveMemberWithOauthId(memberRepository, APPLE, "고유 ID", "스플릿", "소개");

        //when
        final MemberLevelBarResponse 회원_레벨바_응답값 = memberPointQueryService.findMemberLevelBarInfo(스플릿.getId());

        //then
        final int 예상_회원_레벨_구간_길이 = MemberLevel.findByPoint(new Point(0)).get().getLevelRange();

        assertSoftly(
                softly -> {
                    softly.assertThat(회원_레벨바_응답값.getLevelRange()).isEqualTo(예상_회원_레벨_구간_길이);
                    softly.assertThat(회원_레벨바_응답값.getCurrentPoint()).isEqualTo(0);
                    softly.assertThat(회원_레벨바_응답값.getRecentPoint()).isEqualTo(0);
                }
        );
    }

    private void 포인트를_지급한다(final Member member, final int point) {
        memberPointRepository.save(MemberPoint.withoutId(new Point(point), member.getId()));
    }
}

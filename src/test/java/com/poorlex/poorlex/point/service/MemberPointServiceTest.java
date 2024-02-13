package com.poorlex.poorlex.point.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.poorlex.poorlex.member.domain.Member;
import com.poorlex.poorlex.member.domain.MemberLevel;
import com.poorlex.poorlex.member.domain.MemberNickname;
import com.poorlex.poorlex.member.domain.MemberRepository;
import com.poorlex.poorlex.member.domain.Oauth2RegistrationId;
import com.poorlex.poorlex.point.domain.MemberPoint;
import com.poorlex.poorlex.point.domain.MemberPointRepository;
import com.poorlex.poorlex.point.domain.Point;
import com.poorlex.poorlex.point.service.dto.request.PointCreateRequest;
import com.poorlex.poorlex.point.service.dto.response.MemberLevelBarResponse;
import com.poorlex.poorlex.point.service.dto.response.MemberPointResponse;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import com.poorlex.poorlex.support.UsingDataJpaTest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MemberPointServiceTest extends UsingDataJpaTest implements ReplaceUnderScoreTest {

    @Autowired
    private MemberPointRepository memberPointRepository;

    @Autowired
    private MemberRepository memberRepository;

    private MemberPointService memberPointService;

    @BeforeEach
    void setUp() {
        this.memberPointService = new MemberPointService(memberPointRepository, memberRepository);
    }

    @Test
    void 멤버_포인트를_생성한다() {
        //given
        final Member member = memberRepository.save(
            Member.withoutId(Oauth2RegistrationId.APPLE, "oauthId", new MemberNickname("nickname")));
        final PointCreateRequest request = new PointCreateRequest(10);

        //when
        memberPointService.createPoint(member.getId(), request.getPoint());

        //then
        final List<MemberPoint> memberPoints = memberPointRepository.findAll();
        assertSoftly(
            softly -> {
                softly.assertThat(memberPoints).hasSize(1);
                final MemberPoint memberPoint = memberPoints.get(0);
                softly.assertThat(memberPoint.getPoint()).isEqualTo(request.getPoint());
                softly.assertThat(memberPoint.getMemberId()).isEqualTo(member.getId());
            }
        );
    }

    @Test
    void 멤버_포인트의_총합을_조회한다() {
        //given
        final Member member = memberRepository.save(
            Member.withoutId(Oauth2RegistrationId.APPLE, "oauthId", new MemberNickname("nickname")));
        memberPointRepository.save(MemberPoint.withoutId(new Point(10), member.getId()));

        //when
        final MemberPointResponse response = memberPointService.findMemberTotalPoint(member.getId());

        //then
        assertThat(response.getTotalPoint()).isEqualTo(10);
    }

    @Test
    void 멤버_레벨바에_필요한_정보를_조회한다() {
        //given
        final Member member = memberRepository.save(
            Member.withoutId(Oauth2RegistrationId.APPLE, "oauthId", new MemberNickname("nickname")));
        final MemberPoint memberPoint = memberPointRepository.save(
            MemberPoint.withoutId(new Point(10), member.getId())
        );

        //when
        final MemberLevelBarResponse response = memberPointService.findPointsForLevelBar(member.getId());

        //then
        assertSoftly(
            softly -> {
                final int expectRange = MemberLevel.findByPoint(new Point(memberPoint.getPoint()))
                    .orElseThrow(IllegalArgumentException::new)
                    .getLevelRange();

                softly.assertThat(response.getLevelRange()).isEqualTo(expectRange);
                softly.assertThat(response.getCurrentPoint()).isEqualTo(memberPoint.getPoint());
                softly.assertThat(response.getRecentPoint()).isEqualTo(memberPoint.getPoint());
            }
        );
    }

    @Test
    void 멤버_레벨바에_필요한_정보를_조회한다_포인트가_없을_때() {
        //given
        final Member member = memberRepository.save(
            Member.withoutId(Oauth2RegistrationId.APPLE, "oauthId", new MemberNickname("nickname")));

        //when
        final MemberLevelBarResponse response = memberPointService.findPointsForLevelBar(member.getId());

        //then
        assertSoftly(
            softly -> {
                final int expectRange = MemberLevel.LEVEL_1.getLevelRange();

                softly.assertThat(response.getLevelRange()).isEqualTo(expectRange);
                softly.assertThat(response.getCurrentPoint()).isEqualTo(0);
                softly.assertThat(response.getRecentPoint()).isEqualTo(0);
            }
        );
    }
}

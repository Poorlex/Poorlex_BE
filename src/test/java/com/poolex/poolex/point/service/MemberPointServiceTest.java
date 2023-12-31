package com.poolex.poolex.point.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.poolex.poolex.auth.domain.Member;
import com.poolex.poolex.auth.domain.MemberNickname;
import com.poolex.poolex.auth.domain.MemberRepository;
import com.poolex.poolex.point.domain.MemberPoint;
import com.poolex.poolex.point.domain.MemberPointRepository;
import com.poolex.poolex.point.domain.Point;
import com.poolex.poolex.point.service.dto.request.PointCreateRequest;
import com.poolex.poolex.point.service.dto.response.MemberPointResponse;
import com.poolex.poolex.support.ReplaceUnderScoreTest;
import com.poolex.poolex.support.UsingDataJpaTest;
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
        final Member member = memberRepository.save(Member.withoutId("oauthId", new MemberNickname("nickname")));
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
        final Member member = memberRepository.save(Member.withoutId("oauthId", new MemberNickname("nickname")));
        memberPointRepository.save(MemberPoint.withoutId(new Point(10), member.getId()));

        //when
        final MemberPointResponse response = memberPointService.findMemberSumPoint(member.getId());

        //then
        assertThat(response.getTotalPoint()).isEqualTo(10);
    }
}

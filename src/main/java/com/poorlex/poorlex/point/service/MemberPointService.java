package com.poorlex.poorlex.point.service;

import com.poorlex.poorlex.member.domain.MemberLevel;
import com.poorlex.poorlex.member.domain.MemberRepository;
import com.poorlex.poorlex.point.domain.MemberIdAndTotalPointDto;
import com.poorlex.poorlex.point.domain.MemberPoint;
import com.poorlex.poorlex.point.domain.MemberPointRepository;
import com.poorlex.poorlex.point.domain.Point;
import com.poorlex.poorlex.point.service.dto.response.MemberLevelBarResponse;
import com.poorlex.poorlex.point.service.dto.response.MemberPointResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberPointService {

    private final MemberPointRepository memberPointRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void createPoint(final Long memberId, final int point) {
        if (!memberRepository.existsById(memberId)) {
            throw new IllegalArgumentException("해당 ID의 멤버가 존재하지 않습니다.");
        }

        memberPointRepository.save(MemberPoint.withoutId(new Point(point), memberId));
    }

    public MemberPointResponse findMemberTotalPoint(final Long memberId) {
        final int sumPoint = memberPointRepository.findSumByMemberId(memberId);
        final MemberLevel memberLevel = MemberLevel.findByPoint(new Point(sumPoint))
            .orElseThrow(IllegalArgumentException::new);

        return new MemberPointResponse(sumPoint, memberLevel.getNumber());
    }

    public Map<Long, Integer> findMembersTotalPoint(final List<Long> memberIds) {
        return memberPointRepository.findTotalPointsByMemberIdIn(memberIds)
            .stream()
            .collect(Collectors.toMap(MemberIdAndTotalPointDto::getMemberId, MemberIdAndTotalPointDto::getTotalPoint));
    }

    public MemberLevelBarResponse findPointsForLevelBar(final Long memberId) {
        final int totalMemberPoint = memberPointRepository.findSumByMemberId(memberId);
        final MemberLevel memberLevel = getMemberLevel(totalMemberPoint);
        final int currentPoint = getPointAddedFromLevelLowerBound(totalMemberPoint, memberLevel);
        final int memberRecentPoint = getMemberRecentPoint(memberId);

        return new MemberLevelBarResponse(memberLevel.getLevelRange(), currentPoint, memberRecentPoint);
    }

    private MemberLevel getMemberLevel(final int point) {
        return MemberLevel.findByPoint(new Point(point))
            .orElseThrow(IllegalArgumentException::new);
    }

    private int getPointAddedFromLevelLowerBound(final int point, final MemberLevel memberLevel) {
        return point - memberLevel.getLowerBound();
    }

    private int getMemberRecentPoint(final Long memberId) {
        final MemberPoint recentMemberPoint = memberPointRepository.findFirstByMemberIdOrderByIdDesc(memberId)
            .orElse(MemberPoint.withoutId(new Point(0), memberId));

        return recentMemberPoint.getPoint();
    }
}

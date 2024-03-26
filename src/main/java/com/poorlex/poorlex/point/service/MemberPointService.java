package com.poorlex.poorlex.point.service;

import com.poorlex.poorlex.exception.ApiException;
import com.poorlex.poorlex.exception.ExceptionTag;
import com.poorlex.poorlex.user.member.domain.MemberLevel;
import com.poorlex.poorlex.user.member.domain.MemberRepository;
import com.poorlex.poorlex.point.domain.MemberIdAndTotalPointDto;
import com.poorlex.poorlex.point.domain.MemberPoint;
import com.poorlex.poorlex.point.domain.MemberPointRepository;
import com.poorlex.poorlex.point.domain.Point;
import com.poorlex.poorlex.point.service.dto.response.MemberLevelBarResponse;
import com.poorlex.poorlex.point.service.dto.response.MemberPointResponse;
import com.poorlex.poorlex.point.service.dto.response.MyPageLevelInfoResponse;
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
            final String errorMessage = String.format("Id 에 해당하는 회원이 존재하지 않습니다. ( ID : %d )", memberId);
            throw new ApiException(ExceptionTag.MEMBER_FIND, errorMessage);
        }

        memberPointRepository.save(MemberPoint.withoutId(new Point(point), memberId));
    }

    public MemberPointResponse findMemberTotalPoint(final Long memberId) {
        final int sumPoint = memberPointRepository.findSumByMemberId(memberId);
        final MemberLevel memberLevel = getMemberLevel(sumPoint);

        return new MemberPointResponse(sumPoint, memberLevel.getNumber());
    }

    public Map<Long, Integer> findMembersTotalPoint(final List<Long> memberIds) {
        return memberPointRepository.findTotalPointsByMemberIdIn(memberIds)
                .stream()
                .collect(Collectors.toMap(MemberIdAndTotalPointDto::getMemberId,
                                          MemberIdAndTotalPointDto::getTotalPoint));
    }

    public MemberLevelBarResponse findPointsForLevelBar(final Long memberId) {
        final int totalMemberPoint = memberPointRepository.findSumByMemberId(memberId);
        final MemberLevel memberLevel = getMemberLevel(totalMemberPoint);
        final int currentPoint = getPointAddedFromLevelLowerBound(totalMemberPoint, memberLevel);
        final int memberRecentPoint = getMemberRecentPoint(memberId);

        return new MemberLevelBarResponse(memberLevel.getLevelRange(), currentPoint, memberRecentPoint);
    }

    public MyPageLevelInfoResponse findMemberLevelInfo(final Long memberId) {
        final Integer totalMemberPoint = memberPointRepository.findSumByMemberId(memberId);
        final MemberLevel memberLevel = getMemberLevel(totalMemberPoint);
        final Integer getPointForNextLevel = memberLevel.getGetPointForNextLevel(totalMemberPoint);
        return new MyPageLevelInfoResponse(memberLevel.getNumber(), totalMemberPoint, getPointForNextLevel);
    }

    private MemberLevel getMemberLevel(final int point) {
        return MemberLevel.findByPoint(new Point(point))
                .orElseThrow(() -> {
                    final String errorMessage = String.format("포인트에 해당하는 레벨이 존재하지 않습니다. ( 포인트 : %d )", point);
                    return new ApiException(ExceptionTag.MEMBER_LEVEL, errorMessage);
                });
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

package com.poorlex.refactoring.user.point.service.provider.implementation;

import com.poorlex.refactoring.user.member.service.dto.MemberLevelInfoDto;
import com.poorlex.refactoring.user.member.service.provider.MemberLevelInfoProvider;
import com.poorlex.refactoring.user.point.domain.MemberLevel;
import com.poorlex.refactoring.user.point.domain.MemberPointRepository;
import com.poorlex.refactoring.user.point.domain.Point;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberLevelInfoProviderImpl implements MemberLevelInfoProvider {

    private final MemberPointRepository memberPointRepository;

    @Override
    public MemberLevelInfoDto byMemberId(final Long memberId) {
        final int memberTotalPoint = memberPointRepository.findSumByMemberId(memberId);
        final MemberLevel memberLevel = MemberLevel.findByPoint(new Point(memberTotalPoint))
            .orElseThrow(IllegalArgumentException::new);

        return new MemberLevelInfoDto(
            memberLevel.getNumber(),
            memberTotalPoint - memberLevel.getLowerBound(),
            memberLevel.getGetPointForNextLevel(memberTotalPoint)
        );
    }
}

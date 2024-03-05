package com.poorlex.refactoring.user.point.service.provider.implementation;

import com.poorlex.refactoring.user.member.service.provider.LevelProvider;
import com.poorlex.refactoring.user.point.domain.MemberLevel;
import com.poorlex.refactoring.user.point.domain.MemberPointRepository;
import com.poorlex.refactoring.user.point.domain.Point;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LevelProviderImpl implements LevelProvider {

    private final MemberPointRepository memberPointRepository;

    @Override
    public int byMemberId(final Long memberId) {
        final int sumPoint = memberPointRepository.findSumByMemberId(memberId);
        
        return MemberLevel.findByPoint(new Point(sumPoint))
            .orElseThrow(IllegalArgumentException::new)
            .getNumber();
    }
}

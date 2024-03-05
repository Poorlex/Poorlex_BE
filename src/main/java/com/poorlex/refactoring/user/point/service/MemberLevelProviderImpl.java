package com.poorlex.refactoring.user.point.service;

import com.poorlex.refactoring.user.member.service.provider.MemberLevelProvider;
import com.poorlex.refactoring.user.point.domain.MemberPointRepository;
import com.poorlex.refactoring.user.point.domain.Point;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberLevelProviderImpl implements MemberLevelProvider {

    private final MemberPointRepository memberPointRepository;

    @Override
    public int byMemberId(final Long memberId) {
        final int sumByMemberId = memberPointRepository.findSumByMemberId(memberId);

        return new Point(sumByMemberId).getLevel().getNumber();
    }
}

package com.poolex.poolex.point.service;

import com.poolex.poolex.auth.domain.MemberRepository;
import com.poolex.poolex.point.domain.MemberPoint;
import com.poolex.poolex.point.domain.MemberPointRepository;
import com.poolex.poolex.point.domain.Point;
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
}

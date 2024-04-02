package com.poorlex.poorlex.user.point.service;

import com.poorlex.poorlex.exception.ApiException;
import com.poorlex.poorlex.exception.ExceptionTag;
import com.poorlex.poorlex.user.member.domain.MemberRepository;
import com.poorlex.poorlex.user.point.domain.MemberPoint;
import com.poorlex.poorlex.user.point.domain.MemberPointRepository;
import com.poorlex.poorlex.user.point.domain.Point;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberPointCommandService {

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
}

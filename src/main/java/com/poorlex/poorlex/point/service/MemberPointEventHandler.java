package com.poorlex.poorlex.point.service;

import com.poorlex.poorlex.user.member.service.event.MemberDeletedEvent;
import com.poorlex.poorlex.point.domain.MemberPoint;
import com.poorlex.poorlex.point.domain.MemberPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Component
@Transactional(propagation = Propagation.REQUIRED)
@RequiredArgsConstructor
public class MemberPointEventHandler {

    private final MemberPointRepository memberPointRepository;

    @TransactionalEventListener(value = MemberDeletedEvent.class, phase = TransactionPhase.BEFORE_COMMIT)
    public void handle(final MemberDeletedEvent event) {
        final List<MemberPoint> memberPoints = memberPointRepository.findMemberPointsByMemberId(event.getMemberId());
        memberPointRepository.deleteAll(memberPoints);
    }
}

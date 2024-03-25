package com.poorlex.poorlex.alarm.alarmallowance.service;

import com.poorlex.poorlex.alarm.alarmallowance.domain.AlarmAllowance;
import com.poorlex.poorlex.alarm.alarmallowance.domain.AlarmAllowanceRepository;
import com.poorlex.poorlex.user.member.service.event.MemberRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@Transactional(propagation = Propagation.REQUIRED)
@RequiredArgsConstructor
public class AlarmAllowanceEventHandler {

    private final AlarmAllowanceRepository alarmAllowanceRepository;

    @TransactionalEventListener(value = MemberRegisteredEvent.class, phase = TransactionPhase.BEFORE_COMMIT)
    public void handle(final MemberRegisteredEvent event) {
        final AlarmAllowance alarmAllowance = AlarmAllowance.withoutIdWithAllAllowed(event.getMemberId());
        alarmAllowanceRepository.save(alarmAllowance);
    }
}

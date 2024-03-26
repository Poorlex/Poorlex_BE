package com.poorlex.poorlex.weeklybudget.service;

import com.poorlex.poorlex.user.member.service.event.MemberDeletedEvent;
import com.poorlex.poorlex.weeklybudget.domain.WeeklyBudget;
import com.poorlex.poorlex.weeklybudget.domain.WeeklyBudgetRepository;
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
public class WeeklyBudgetEventHandler {

    private final WeeklyBudgetRepository weeklyBudgetRepository;

    @TransactionalEventListener(value = MemberDeletedEvent.class, phase = TransactionPhase.BEFORE_COMMIT)
    public void handle(final MemberDeletedEvent event) {
        final List<WeeklyBudget> weeklyBudgets = weeklyBudgetRepository.findWeeklyBudgetsByMemberId(event.getMemberId());
        weeklyBudgetRepository.deleteAllInBatch(weeklyBudgets);
    }
}

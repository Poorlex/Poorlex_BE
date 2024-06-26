package com.poorlex.poorlex.consumption.weeklybudget.service;

import com.poorlex.poorlex.consumption.weeklybudget.domain.WeeklyBudget;
import com.poorlex.poorlex.consumption.weeklybudget.domain.WeeklyBudgetRepository;
import com.poorlex.poorlex.consumption.weeklybudget.service.event.WeeklyBudgetMemberDeletedEvent;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Transactional(propagation = Propagation.REQUIRED)
@RequiredArgsConstructor
public class WeeklyBudgetEventHandler {

    private final WeeklyBudgetRepository weeklyBudgetRepository;

    @TransactionalEventListener(value = WeeklyBudgetMemberDeletedEvent.class, phase = TransactionPhase.BEFORE_COMMIT)
    public void handle(final WeeklyBudgetMemberDeletedEvent event) {
        final List<WeeklyBudget> weeklyBudgets = weeklyBudgetRepository.findWeeklyBudgetsByMemberId(event.getMemberId());
        weeklyBudgetRepository.deleteAllInBatch(weeklyBudgets);
    }
}

package com.poolex.poolex.weeklybudget.service;

import com.poolex.poolex.auth.domain.MemberRepository;
import com.poolex.poolex.weeklybudget.domain.WeeklyBudget;
import com.poolex.poolex.weeklybudget.domain.WeeklyBudgetAmount;
import com.poolex.poolex.weeklybudget.domain.WeeklyBudgetDuration;
import com.poolex.poolex.weeklybudget.domain.WeeklyBudgetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WeeklyBudgetService {

    private final WeeklyBudgetRepository weeklyBudgetRepository;
    private final MemberRepository memberRepository;

    public void createBudget(final Long memberId, final int budget) {
        if (!memberRepository.existsById(memberId)) {
            throw new IllegalArgumentException("Id에 해당하는 멤버가 없습니다.");
        }
        final WeeklyBudgetAmount amount = new WeeklyBudgetAmount(budget);
        final WeeklyBudgetDuration duration = WeeklyBudgetDuration.current();
        final WeeklyBudget weeklyBudget = WeeklyBudget.withoutId(amount, duration, memberId);

        weeklyBudgetRepository.save(weeklyBudget);
    }
}

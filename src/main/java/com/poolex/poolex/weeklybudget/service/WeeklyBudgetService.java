package com.poolex.poolex.weeklybudget.service;

import com.poolex.poolex.auth.domain.MemberRepository;
import com.poolex.poolex.weeklybudget.domain.WeeklyBudget;
import com.poolex.poolex.weeklybudget.domain.WeeklyBudgetAmount;
import com.poolex.poolex.weeklybudget.domain.WeeklyBudgetDuration;
import com.poolex.poolex.weeklybudget.domain.WeeklyBudgetRepository;
import com.poolex.poolex.weeklybudget.service.dto.response.WeeklyBudgetResponse;
import java.time.LocalDate;
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
        validateMemberId(memberId);
        final WeeklyBudgetAmount amount = new WeeklyBudgetAmount(budget);
        final WeeklyBudgetDuration duration = WeeklyBudgetDuration.current();
        final WeeklyBudget weeklyBudget = WeeklyBudget.withoutId(amount, duration, memberId);

        weeklyBudgetRepository.save(weeklyBudget);
    }

    public WeeklyBudgetResponse findCurrentBudgetByMemberIdAndDate(final Long memberId, final LocalDate date) {
        validateMemberId(memberId);
        final WeeklyBudget weeklyBudget = weeklyBudgetRepository.findByMemberIdAndCurrentDate(memberId, date)
            .orElse(null);

        return WeeklyBudgetResponse.from(weeklyBudget);
    }

    private void validateMemberId(final Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new IllegalArgumentException("Id에 해당하는 멤버가 없습니다.");
        }
    }
}

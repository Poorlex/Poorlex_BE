package com.poolex.poolex.weeklybudget.service;

import com.poolex.poolex.auth.domain.MemberRepository;
import com.poolex.poolex.expenditure.domain.ExpenditureRepository;
import com.poolex.poolex.weeklybudget.domain.WeeklyBudget;
import com.poolex.poolex.weeklybudget.domain.WeeklyBudgetAmount;
import com.poolex.poolex.weeklybudget.domain.WeeklyBudgetDuration;
import com.poolex.poolex.weeklybudget.domain.WeeklyBudgetRepository;
import com.poolex.poolex.weeklybudget.service.dto.response.WeeklyBudgetLeftResponse;
import com.poolex.poolex.weeklybudget.service.dto.response.WeeklyBudgetResponse;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WeeklyBudgetService {

    private final WeeklyBudgetRepository weeklyBudgetRepository;
    private final ExpenditureRepository expenditureRepository;
    private final MemberRepository memberRepository;

    public void createBudget(final Long memberId, final int budget) {
        validateMemberId(memberId);
        final WeeklyBudgetAmount amount = new WeeklyBudgetAmount(budget);
        final WeeklyBudgetDuration duration = WeeklyBudgetDuration.current();
        final WeeklyBudget weeklyBudget = WeeklyBudget.withoutId(amount, duration, memberId);

        weeklyBudgetRepository.save(weeklyBudget);
    }

    public WeeklyBudgetResponse findCurrentBudgetByMemberIdAndDate(final Long memberId, final LocalDateTime date) {
        validateMemberId(memberId);
        
        return weeklyBudgetRepository.findByMemberIdAndCurrentDate(memberId, date)
            .map(findWeeklyBudget -> WeeklyBudgetResponse.exist(findWeeklyBudget, findWeeklyBudget.getDDay(date)))
            .orElseGet(WeeklyBudgetResponse::empty);
    }

    private void validateMemberId(final Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new IllegalArgumentException("Id에 해당하는 멤버가 없습니다.");
        }
    }

    public WeeklyBudgetLeftResponse findCurrentBudgetLeftByMemberIdAndDate(final Long memberId,
                                                                           final LocalDateTime date) {
        validateMemberId(memberId);

        final WeeklyBudget weeklyBudget = weeklyBudgetRepository.findByMemberIdAndCurrentDate(memberId, date)
            .orElse(null);
        if (Objects.isNull(weeklyBudget)) {
            return WeeklyBudgetLeftResponse.withNullWeeklyBudget();
        }

        final int sumExpenditure = getSumExpenditureByMemberIdInDuration(memberId, weeklyBudget.getDuration());
        return WeeklyBudgetLeftResponse.from(weeklyBudget, sumExpenditure);
    }

    public int getSumExpenditureByMemberIdInDuration(final Long memberId, final WeeklyBudgetDuration duration) {
        final LocalDateTime start = duration.getStart();
        final LocalDateTime end = duration.getEnd();
        return expenditureRepository.findSumExpenditureByMemberIdAndBetween(memberId, start, end);
    }
}

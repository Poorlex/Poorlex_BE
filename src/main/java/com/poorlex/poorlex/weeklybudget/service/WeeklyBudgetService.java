package com.poorlex.poorlex.weeklybudget.service;

import com.poorlex.poorlex.expenditure.domain.ExpenditureRepository;
import com.poorlex.poorlex.member.domain.MemberRepository;
import com.poorlex.poorlex.weeklybudget.domain.WeeklyBudget;
import com.poorlex.poorlex.weeklybudget.domain.WeeklyBudgetAmount;
import com.poorlex.poorlex.weeklybudget.domain.WeeklyBudgetDuration;
import com.poorlex.poorlex.weeklybudget.domain.WeeklyBudgetRepository;
import com.poorlex.poorlex.weeklybudget.service.dto.response.WeeklyBudgetLeftResponse;
import com.poorlex.poorlex.weeklybudget.service.dto.response.WeeklyBudgetResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WeeklyBudgetService {

    private final WeeklyBudgetRepository weeklyBudgetRepository;
    private final ExpenditureRepository expenditureRepository;
    private final MemberRepository memberRepository;

    public void createBudget(final Long memberId, final Long budget) {
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

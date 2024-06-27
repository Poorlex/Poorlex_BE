package com.poorlex.poorlex.consumption.weeklybudget.service;

import com.poorlex.poorlex.consumption.weeklybudget.domain.WeeklyBudget;
import com.poorlex.poorlex.consumption.weeklybudget.domain.WeeklyBudgetRepository;
import com.poorlex.poorlex.consumption.weeklybudget.service.dto.response.WeeklyBudgetLeftResponse;
import com.poorlex.poorlex.consumption.weeklybudget.service.dto.response.WeeklyBudgetResponse;
import com.poorlex.poorlex.consumption.weeklybudget.service.provider.MemberExistenceProvider;
import com.poorlex.poorlex.consumption.weeklybudget.service.provider.TotalExpenditureProvider;
import com.poorlex.poorlex.exception.ApiException;
import com.poorlex.poorlex.exception.ExceptionTag;
import java.time.LocalDate;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WeeklyBudgetQueryService {

    private final WeeklyBudgetRepository weeklyBudgetRepository;
    private final MemberExistenceProvider memberExistenceProvider;
    private final TotalExpenditureProvider totalExpenditureProvider;

    public WeeklyBudgetResponse findWeeklyBudgetByMemberId(final Long memberId) {
        return weeklyBudgetRepository.findByMemberId(memberId)
                .map(WeeklyBudgetResponse::exist)
                .orElseGet(WeeklyBudgetResponse::empty);
    }

    private void validateMemberId(final Long memberId) {
        if (!memberExistenceProvider.byMemberId(memberId)) {
            final String errorMessage = String.format("Id 에 해당하는 회원이 존재하지 않습니다. ( ID : %d )", memberId);
            throw new ApiException(ExceptionTag.MEMBER_FIND, errorMessage);
        }
    }

    public WeeklyBudgetLeftResponse findCurrentWeeklyBudgetLeftByMemberId(final Long memberId) {
        return findWeeklyBudgetLeftByMemberIdAndDate(memberId, LocalDate.now());
    }

    public WeeklyBudgetLeftResponse findWeeklyBudgetLeftByMemberIdAndDate(final Long memberId,
                                                                          final LocalDate date) {
        validateMemberId(memberId);

        final WeeklyBudget weeklyBudget = weeklyBudgetRepository.findByMemberId(memberId)
                .orElse(null);
        if (Objects.isNull(weeklyBudget)) {
            return WeeklyBudgetLeftResponse.withNullWeeklyBudget();
        }

        final Long sumExpenditure = getSumExpenditureByMemberIdInDuration(memberId, date);
        return WeeklyBudgetLeftResponse.from(weeklyBudget, sumExpenditure);
    }

    public Long getSumExpenditureByMemberIdInDuration(final Long memberId, LocalDate date) {
        final LocalDate start = date.minusDays(LocalDate.now().getDayOfWeek().getValue() + 1);
        final LocalDate end = date.plusDays(8 - LocalDate.now().getDayOfWeek().getValue());
        return totalExpenditureProvider.byMemberIdBetween(memberId, start, end);
    }
}

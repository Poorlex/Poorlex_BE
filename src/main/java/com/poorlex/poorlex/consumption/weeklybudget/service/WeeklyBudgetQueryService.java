package com.poorlex.poorlex.consumption.weeklybudget.service;

import com.poorlex.poorlex.consumption.weeklybudget.domain.WeeklyBudget;
import com.poorlex.poorlex.consumption.weeklybudget.domain.WeeklyBudgetDuration;
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

    public WeeklyBudgetResponse findCurrentWeeklyBudgetByMemberId(final Long memberId) {
        return findWeeklyBudgetByMemberIdAndDate(memberId, LocalDate.now());
    }

    public WeeklyBudgetResponse findWeeklyBudgetByMemberIdAndDate(final Long memberId, final LocalDate date) {
        validateMemberId(memberId);

        return weeklyBudgetRepository.findByMemberIdAndCurrentDate(memberId, date)
                .map(findWeeklyBudget -> WeeklyBudgetResponse.exist(findWeeklyBudget, findWeeklyBudget.getDDay(date)))
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

        final WeeklyBudget weeklyBudget = weeklyBudgetRepository.findByMemberIdAndCurrentDate(memberId, date)
                .orElse(null);
        if (Objects.isNull(weeklyBudget)) {
            return WeeklyBudgetLeftResponse.withNullWeeklyBudget();
        }

        final Long sumExpenditure = getSumExpenditureByMemberIdInDuration(memberId, weeklyBudget.getDuration());
        return WeeklyBudgetLeftResponse.from(weeklyBudget, sumExpenditure);
    }

    public Long getSumExpenditureByMemberIdInDuration(final Long memberId, final WeeklyBudgetDuration duration) {
        final LocalDate start = LocalDate.from(duration.getStart());
        final LocalDate end = LocalDate.from(duration.getEnd());
        return totalExpenditureProvider.byMemberIdBetween(memberId, start, end);
    }
}

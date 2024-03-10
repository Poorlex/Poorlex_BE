package com.poorlex.poorlex.weeklybudget.service.dto.response;

import com.poorlex.poorlex.weeklybudget.domain.WeeklyBudget;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@RequiredArgsConstructor
@Getter
public class WeeklyBudgetLeftResponse {

    private final boolean exist;

    private final Long amount;

    public static WeeklyBudgetLeftResponse withNullWeeklyBudget() {
        return new WeeklyBudgetLeftResponse(false, 0L);
    }

    public static WeeklyBudgetLeftResponse from(final WeeklyBudget weeklyBudget, final int sumExpenditures) {
        if (Objects.isNull(weeklyBudget)) {
            return new WeeklyBudgetLeftResponse(false, 0L);
        }
        return new WeeklyBudgetLeftResponse(true, weeklyBudget.getAmount() - sumExpenditures);
    }
}

package com.poorlex.poorlex.consumption.weeklybudget.service.dto.response;

import com.poorlex.poorlex.consumption.weeklybudget.domain.WeeklyBudget;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class WeeklyBudgetResponse {

    private static final boolean BUDGET_NOT_EXIST = false;
    private static final boolean BUDGET_EXIST = true;

    private final boolean exist;
    private final Long amount;
    private final Long daysBeforeEnd;

    public static WeeklyBudgetResponse exist(final WeeklyBudget weeklyBudget, final Long dDay) {
        return new WeeklyBudgetResponse(BUDGET_EXIST, weeklyBudget.getAmount(), dDay);
    }

    public static WeeklyBudgetResponse empty() {
        return new WeeklyBudgetResponse(BUDGET_NOT_EXIST, 0L, 0L);
    }
}

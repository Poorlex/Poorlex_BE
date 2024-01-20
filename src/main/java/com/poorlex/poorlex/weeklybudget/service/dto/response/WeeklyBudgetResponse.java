package com.poorlex.poorlex.weeklybudget.service.dto.response;

import com.poorlex.poorlex.weeklybudget.domain.WeeklyBudget;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class WeeklyBudgetResponse {

    private static final boolean BUDGET_NOT_EXIST = false;
    private static final boolean BUDGET_EXIST = true;

    private final boolean exist;
    private final int amount;
    private final long dDay;

    public static WeeklyBudgetResponse exist(final WeeklyBudget weeklyBudget, final long dDay) {
        return new WeeklyBudgetResponse(BUDGET_EXIST, weeklyBudget.getAmount(), dDay);
    }

    public static WeeklyBudgetResponse empty() {
        return new WeeklyBudgetResponse(BUDGET_NOT_EXIST, 0, 0);
    }
}

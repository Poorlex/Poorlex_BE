package com.poorlex.poorlex.weeklybudget.service.dto.response;

import com.poorlex.poorlex.weeklybudget.domain.WeeklyBudget;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class WeeklyBudgetLeftResponse {

    private final boolean exist;

    private final int amount;

    public static WeeklyBudgetLeftResponse withNullWeeklyBudget() {
        return new WeeklyBudgetLeftResponse(false, 0);
    }

    public static WeeklyBudgetLeftResponse from(final WeeklyBudget weeklyBudget, final int sumExpenditures) {
        if (Objects.isNull(weeklyBudget)) {
            return new WeeklyBudgetLeftResponse(false, 0);
        }
        return new WeeklyBudgetLeftResponse(true, weeklyBudget.getAmount() - sumExpenditures);
    }
}

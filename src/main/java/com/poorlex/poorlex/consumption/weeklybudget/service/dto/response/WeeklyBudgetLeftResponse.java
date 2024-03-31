package com.poorlex.poorlex.consumption.weeklybudget.service.dto.response;

import com.poorlex.poorlex.consumption.weeklybudget.domain.WeeklyBudget;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class WeeklyBudgetLeftResponse {

    private final boolean exist;

    private final Long amount;

    public static WeeklyBudgetLeftResponse withNullWeeklyBudget() {
        return new WeeklyBudgetLeftResponse(false, 0L);
    }

    public static WeeklyBudgetLeftResponse from(final WeeklyBudget weeklyBudget, final Long sumExpenditures) {
        if (Objects.isNull(weeklyBudget)) {
            return new WeeklyBudgetLeftResponse(false, 0L);
        }
        return new WeeklyBudgetLeftResponse(true, weeklyBudget.getAmount() - sumExpenditures);
    }
}

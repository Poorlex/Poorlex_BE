package com.poolex.poolex.weeklybudget.service.dto.response;

import com.poolex.poolex.weeklybudget.domain.WeeklyBudget;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class WeeklyBudgetResponse {

    private final boolean exist;

    private final int amount;

    public static WeeklyBudgetResponse from(final WeeklyBudget weeklyBudget) {
        if (Objects.isNull(weeklyBudget)) {
            return new WeeklyBudgetResponse(false, 0);
        }
        return new WeeklyBudgetResponse(true, weeklyBudget.getAmount());
    }
}

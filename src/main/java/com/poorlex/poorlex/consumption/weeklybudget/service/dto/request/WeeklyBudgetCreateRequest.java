package com.poorlex.poorlex.consumption.weeklybudget.service.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WeeklyBudgetCreateRequest {

    private Long budget;

    public WeeklyBudgetCreateRequest(final Long budget) {
        this.budget = budget;
    }

    public Long getBudget() {
        return budget;
    }
}

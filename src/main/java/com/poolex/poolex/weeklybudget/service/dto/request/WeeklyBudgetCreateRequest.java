package com.poolex.poolex.weeklybudget.service.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WeeklyBudgetCreateRequest {

    private int budget;

    public WeeklyBudgetCreateRequest(final int budget) {
        this.budget = budget;
    }
}

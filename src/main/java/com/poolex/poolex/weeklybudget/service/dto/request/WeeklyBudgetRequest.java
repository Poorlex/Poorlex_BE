package com.poolex.poolex.weeklybudget.service.dto.request;

import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WeeklyBudgetRequest {

    private LocalDate date;

    public WeeklyBudgetRequest(final LocalDate date) {
        this.date = date;
    }
}

package com.poorlex.poorlex.consumption.weeklybudget.service.dto.request;

import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WeeklyBudgetLeftRequest {

    private LocalDate date;

    public WeeklyBudgetLeftRequest(final LocalDate date) {
        this.date = date;
    }
}

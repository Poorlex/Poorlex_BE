package com.poolex.poolex.weeklybudget.service.dto.request;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WeeklyBudgetLeftRequest {

    private LocalDateTime date;

    public WeeklyBudgetLeftRequest(final LocalDateTime date) {
        this.date = date;
    }
}

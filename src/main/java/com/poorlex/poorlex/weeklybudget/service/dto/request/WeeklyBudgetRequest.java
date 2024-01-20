package com.poorlex.poorlex.weeklybudget.service.dto.request;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WeeklyBudgetRequest {

    private LocalDateTime dateTime;

    public WeeklyBudgetRequest(final LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}

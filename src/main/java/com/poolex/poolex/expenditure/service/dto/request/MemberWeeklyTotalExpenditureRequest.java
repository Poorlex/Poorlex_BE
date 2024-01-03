package com.poolex.poolex.expenditure.service.dto.request;

import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberWeeklyTotalExpenditureRequest {

    private LocalDate date;

    public MemberWeeklyTotalExpenditureRequest(final LocalDate date) {
        this.date = date;
    }
}

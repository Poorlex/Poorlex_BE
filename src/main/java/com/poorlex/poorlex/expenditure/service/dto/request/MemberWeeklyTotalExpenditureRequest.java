package com.poorlex.poorlex.expenditure.service.dto.request;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberWeeklyTotalExpenditureRequest {

    private LocalDateTime dateTime;

    public MemberWeeklyTotalExpenditureRequest(final LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}

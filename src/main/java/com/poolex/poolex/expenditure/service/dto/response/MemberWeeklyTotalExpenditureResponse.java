package com.poolex.poolex.expenditure.service.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberWeeklyTotalExpenditureResponse {

    private int amount;

    public MemberWeeklyTotalExpenditureResponse(final int amount) {
        this.amount = amount;
    }
}

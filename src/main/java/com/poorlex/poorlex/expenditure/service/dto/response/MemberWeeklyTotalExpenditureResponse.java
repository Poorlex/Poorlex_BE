package com.poorlex.poorlex.expenditure.service.dto.response;

import lombok.Getter;

@Getter
public class MemberWeeklyTotalExpenditureResponse {

    private final int amount;

    public MemberWeeklyTotalExpenditureResponse(final int amount) {
        this.amount = amount;
    }
}

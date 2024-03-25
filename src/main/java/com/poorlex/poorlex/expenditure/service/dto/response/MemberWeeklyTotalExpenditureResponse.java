package com.poorlex.poorlex.expenditure.service.dto.response;

import lombok.Getter;

@Getter
public class MemberWeeklyTotalExpenditureResponse {

    private final Long amount;

    public MemberWeeklyTotalExpenditureResponse(final Long amount) {
        this.amount = amount;
    }
}

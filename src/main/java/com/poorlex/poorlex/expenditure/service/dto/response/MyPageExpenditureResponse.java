package com.poorlex.poorlex.expenditure.service.dto.response;

import java.time.LocalDate;
import lombok.Getter;

@Getter
public class MyPageExpenditureResponse {

    private final Long id;
    private final LocalDate date;
    private final long amount;
    private final String imageUrl;

    public MyPageExpenditureResponse(final Long id,
                                     final LocalDate date,
                                     final long amount,
                                     final String imageUrl) {
        this.id = id;
        this.date = date;
        this.amount = amount;
        this.imageUrl = imageUrl;
    }

    public static MyPageExpenditureResponse from(final ExpenditureResponse expenditureResponse) {
        return new MyPageExpenditureResponse(
            expenditureResponse.getId(),
            LocalDate.from(expenditureResponse.getDate()),
            expenditureResponse.getAmount(),
            expenditureResponse.getImageUrls().get(0)
        );
    }
}

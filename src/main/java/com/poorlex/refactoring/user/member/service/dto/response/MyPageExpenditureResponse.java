package com.poorlex.refactoring.user.member.service.dto.response;

import com.poorlex.refactoring.user.member.service.dto.MyPageExpenditureDto;
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

    public static MyPageExpenditureResponse from(final MyPageExpenditureDto expenditureDto) {
        return new MyPageExpenditureResponse(
            expenditureDto.getId(),
            LocalDate.from(expenditureDto.getDate()),
            expenditureDto.getAmount(),
            expenditureDto.getImageUrl()
        );
    }
}

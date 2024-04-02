package com.poorlex.poorlex.user.member.service.dto;

import java.time.LocalDate;
import lombok.Getter;

@Getter
public class ExpenditureDto {

    private final Long id;
    private final LocalDate date;
    private final Long amount;
    private final String imageUrl;

    public ExpenditureDto(final Long id, final LocalDate date, final Long amount, final String imageUrl) {
        this.id = id;
        this.date = date;
        this.amount = amount;
        this.imageUrl = imageUrl;
    }
}

package com.poorlex.poorlex.consumption.expenditure.service.dto.request;

import java.time.LocalDate;
import lombok.Getter;

@Getter
public class ExpenditureCreateRequest {

    private final long amount;
    private final String description;
    private final LocalDate date;

    public ExpenditureCreateRequest(final long amount, final String description, final LocalDate date) {
        this.amount = amount;
        this.description = description;
        this.date = date;
    }
}

package com.poorlex.poorlex.expenditure.service.dto.request;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class ExpenditureCreateRequest {

    private final long amount;
    private final String description;
    private final LocalDateTime dateTime;

    public ExpenditureCreateRequest(final long amount, final String description, final LocalDateTime dateTime) {
        this.amount = amount;
        this.description = description;
        this.dateTime = dateTime;
    }
}

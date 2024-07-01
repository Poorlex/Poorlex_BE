package com.poorlex.poorlex.consumption.expenditure.service.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@RequiredArgsConstructor
@Getter
public class ExpenditureUpdateRequest {

    private final LocalDate date;
    private final Long amount;
    private final String description;
}

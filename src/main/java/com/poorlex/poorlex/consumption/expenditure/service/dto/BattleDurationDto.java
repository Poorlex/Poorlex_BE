package com.poorlex.poorlex.consumption.expenditure.service.dto;

import java.time.LocalDate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class BattleDurationDto {

    private final LocalDate startDate;
    private final LocalDate endDate;
}

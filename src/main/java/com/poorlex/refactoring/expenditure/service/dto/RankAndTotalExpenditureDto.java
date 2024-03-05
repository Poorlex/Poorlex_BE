package com.poorlex.refactoring.expenditure.service.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RankAndTotalExpenditureDto {

    private final int rank;
    private final Long totalExpenditure;
}

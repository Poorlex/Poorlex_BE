package com.poorlex.poorlex.expenditure.service.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ExpenditureUpdateRequest {

    private final Long amount;
    private final String description;
}

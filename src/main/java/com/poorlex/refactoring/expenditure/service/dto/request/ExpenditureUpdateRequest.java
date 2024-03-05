package com.poorlex.refactoring.expenditure.service.dto.request;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ExpenditureUpdateRequest {

    private final long amount;
    private final String description;
    private final List<String> imageUrls;
}

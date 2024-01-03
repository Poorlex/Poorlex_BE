package com.poolex.poolex.expenditure.service.dto.request;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ExpenditureCreateRequest {

    private final long amount;
    private final String description;
    private final List<String> imageUrls;
}

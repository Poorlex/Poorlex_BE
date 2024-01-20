package com.poorlex.poorlex.expenditure.service.dto.request;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ExpenditureCreateRequest {

    private final long amount;
    private final String description;
    private final List<String> imageUrls;
    private final LocalDateTime dateTime;
}

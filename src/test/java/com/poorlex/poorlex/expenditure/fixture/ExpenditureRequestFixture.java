package com.poorlex.poorlex.expenditure.fixture;

import com.poorlex.poorlex.expenditure.service.dto.request.ExpenditureCreateRequest;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class ExpenditureRequestFixture {

    private ExpenditureRequestFixture() {
    }

    public static ExpenditureCreateRequest getSimpleCreateRequest() {
        return new ExpenditureCreateRequest(
            1000L,
            "description",
            LocalDateTime.now().truncatedTo(ChronoUnit.MICROS)
        );
    }
}

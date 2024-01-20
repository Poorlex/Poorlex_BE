package com.poorlex.poorlex.expenditure.fixture;

import com.poorlex.poorlex.expenditure.service.dto.request.ExpenditureCreateRequest;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class ExpenditureRequestFixture {

    private ExpenditureRequestFixture() {
    }

    public static ExpenditureCreateRequest getSimpleCreateRequest() {
        return new ExpenditureCreateRequest(
            1000L,
            "description",
            List.of("imageUrl1", "imageUrl2"),
            LocalDateTime.now().truncatedTo(ChronoUnit.MICROS)
        );
    }
}

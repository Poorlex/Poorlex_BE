package com.poorlex.poorlex.expenditure.fixture;

import com.poorlex.poorlex.expenditure.service.dto.request.ExpenditureCreateRequest;
import java.time.LocalDate;

public class ExpenditureRequestFixture {

    private ExpenditureRequestFixture() {
    }

    public static ExpenditureCreateRequest getSimpleCreateRequest() {
        return new ExpenditureCreateRequest(
                1000L,
                "description",
                LocalDate.now()
        );
    }

    public static ExpenditureCreateRequest getWithDate(final LocalDate date) {
        return new ExpenditureCreateRequest(
                1000L,
                "description",
                date
        );
    }
}

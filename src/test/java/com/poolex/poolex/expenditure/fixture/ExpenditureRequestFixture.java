package com.poolex.poolex.expenditure.fixture;

import com.poolex.poolex.expenditure.service.dto.ExpenditureCreateRequest;
import java.util.List;

public class ExpenditureRequestFixture {

    private ExpenditureRequestFixture() {
    }

    public static ExpenditureCreateRequest getSimpleCreateRequest() {
        return new ExpenditureCreateRequest(1000L, "description", List.of("imageUrl1", "imageUrl2"));
    }
}

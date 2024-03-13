package com.poorlex.poorlex.expenditure.service.dto.response;

import com.poorlex.poorlex.expenditure.domain.Expenditure;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public class ExpenditureResponse {

    private final Long id;
    private final LocalDate date;
    private final long amount;
    private final String description;
    private final String mainImageUrl;
    private final String subImageUrl;

    public ExpenditureResponse(final Long id,
                               final LocalDate date,
                               final long amount,
                               final String description,
                               final String mainImageUrl,
                               final String subImageUrl) {
        this.id = id;
        this.date = date;
        this.amount = amount;
        this.description = description;
        this.mainImageUrl = mainImageUrl;
        this.subImageUrl = subImageUrl;
    }

    public static ExpenditureResponse from(final Expenditure expenditure) {
        return new ExpenditureResponse(
                expenditure.getId(),
                LocalDate.from(expenditure.getDate()),
                expenditure.getAmount(),
                expenditure.getDescription(),
                expenditure.getMainImageUrl(),
                expenditure.getSubImageUrl().orElse(null)
        );
    }
}

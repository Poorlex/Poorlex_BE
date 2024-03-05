package com.poorlex.refactoring.expenditure.service.dto.response;

import com.poorlex.refactoring.expenditure.domain.Expenditure;
import com.poorlex.refactoring.expenditure.domain.ExpenditureCertificationImageUrl;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;

@Getter
public class ExpenditureResponse {

    private final Long id;
    private final LocalDate date;
    private final long amount;
    private final String description;
    private final List<String> imageUrls;

    public ExpenditureResponse(final Long id,
                               final LocalDate date,
                               final long amount,
                               final String description,
                               final List<String> imageUrls) {
        this.id = id;
        this.date = date;
        this.amount = amount;
        this.description = description;
        this.imageUrls = imageUrls;
    }

    public static ExpenditureResponse from(final Expenditure expenditure) {
        final List<String> imageUrls = expenditure.getImageUrls()
            .getUrls()
            .stream()
            .map(ExpenditureCertificationImageUrl::getValue)
            .toList();

        return new ExpenditureResponse(
            expenditure.getId(),
            LocalDate.from(expenditure.getDate()),
            expenditure.getAmount(),
            expenditure.getDescription(),
            imageUrls
        );
    }
}

package com.poorlex.refactoring.expenditure.service.mapper;

import com.poorlex.refactoring.expenditure.domain.Expenditure;
import com.poorlex.refactoring.expenditure.domain.ExpenditureAmount;
import com.poorlex.refactoring.expenditure.domain.ExpenditureCertificationImageUrls;
import com.poorlex.refactoring.expenditure.domain.ExpenditureDescription;
import com.poorlex.refactoring.expenditure.service.dto.request.ExpenditureCreateRequest;
import java.time.LocalDate;
import java.util.ArrayList;

public class ExpenditureMapper {

    private ExpenditureMapper() {

    }

    public static Expenditure mappedBy(final Long memberId, final ExpenditureCreateRequest request) {
        final ExpenditureAmount amount = new ExpenditureAmount(request.getAmount());
        final ExpenditureDescription description = new ExpenditureDescription(request.getDescription());
        final ExpenditureCertificationImageUrls imageUrls = new ExpenditureCertificationImageUrls(new ArrayList<>());
        final LocalDate date = request.getDate();

        return Expenditure.withoutId(amount, memberId, date, description, imageUrls);
    }
}

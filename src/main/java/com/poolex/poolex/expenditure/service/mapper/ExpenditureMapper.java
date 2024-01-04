package com.poolex.poolex.expenditure.service.mapper;

import com.poolex.poolex.expenditure.domain.Expenditure;
import com.poolex.poolex.expenditure.domain.ExpenditureAmount;
import com.poolex.poolex.expenditure.domain.ExpenditureCertificationImageUrl;
import com.poolex.poolex.expenditure.domain.ExpenditureCertificationImageUrls;
import com.poolex.poolex.expenditure.domain.ExpenditureDescription;
import com.poolex.poolex.expenditure.service.dto.request.ExpenditureCreateRequest;
import java.time.LocalDateTime;
import java.util.List;

public class ExpenditureMapper {

    private ExpenditureMapper() {

    }

    public static Expenditure createRequestToExpenditure(final Long memberId, final ExpenditureCreateRequest request) {
        final ExpenditureAmount amount = new ExpenditureAmount(request.getAmount());
        final ExpenditureDescription description = new ExpenditureDescription(request.getDescription());
        final ExpenditureCertificationImageUrls imageUrls = createImageUrls(request);
        final LocalDateTime dateTime = request.getDateTime();

        return Expenditure.withoutId(amount, memberId, dateTime, description, imageUrls);
    }

    private static ExpenditureCertificationImageUrls createImageUrls(final ExpenditureCreateRequest request) {
        final List<ExpenditureCertificationImageUrl> imageUrlList = request.getImageUrls()
            .stream()
            .map(ExpenditureCertificationImageUrl::withoutIdAndExpenditure)
            .toList();

        return new ExpenditureCertificationImageUrls(imageUrlList);
    }
}

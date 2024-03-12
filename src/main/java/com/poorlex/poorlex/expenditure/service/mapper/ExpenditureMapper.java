package com.poorlex.poorlex.expenditure.service.mapper;

import com.poorlex.poorlex.expenditure.domain.Expenditure;
import com.poorlex.poorlex.expenditure.domain.ExpenditureAmount;
import com.poorlex.poorlex.expenditure.domain.ExpenditureCertificationImageUrl;
import com.poorlex.poorlex.expenditure.domain.ExpenditureCertificationImageUrls;
import com.poorlex.poorlex.expenditure.domain.ExpenditureDescription;
import com.poorlex.poorlex.expenditure.service.dto.request.ExpenditureCreateRequest;
import com.poorlex.poorlex.expenditure.service.dto.request.ExpenditureUpdateRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ExpenditureMapper {

    private ExpenditureMapper() {

    }

    public static Expenditure createRequestToExpenditure(final Long memberId, final ExpenditureCreateRequest request) {
        final ExpenditureAmount amount = new ExpenditureAmount(request.getAmount());
        final ExpenditureDescription description = new ExpenditureDescription(request.getDescription());
        final LocalDate date = request.getDate();
        final ExpenditureCertificationImageUrls imageUrls = new ExpenditureCertificationImageUrls(
            new ArrayList<>());
        return Expenditure.withoutId(amount, memberId, date, description, imageUrls);
    }

    public static Expenditure createRequestToExpenditure(final Long memberId, final ExpenditureUpdateRequest request) {
        final ExpenditureAmount amount = new ExpenditureAmount(request.getAmount());
        final ExpenditureDescription description = new ExpenditureDescription(request.getDescription());
        final ExpenditureCertificationImageUrls imageUrls = createImageUrls(request);

        return Expenditure.withoutId(amount, memberId, LocalDate.now(), description, imageUrls);
    }

    private static ExpenditureCertificationImageUrls createImageUrls(final ExpenditureUpdateRequest request) {
        final List<ExpenditureCertificationImageUrl> imageUrlList = request.getImageUrls()
            .stream()
            .map(ExpenditureCertificationImageUrl::withoutIdAndExpenditure)
            .toList();

        return new ExpenditureCertificationImageUrls(imageUrlList);
    }
}

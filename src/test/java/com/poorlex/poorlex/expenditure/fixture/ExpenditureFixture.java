package com.poorlex.poorlex.expenditure.fixture;

import com.poorlex.poorlex.expenditure.domain.Expenditure;
import com.poorlex.poorlex.expenditure.domain.ExpenditureAmount;
import com.poorlex.poorlex.expenditure.domain.ExpenditureCertificationImageUrl;
import com.poorlex.poorlex.expenditure.domain.ExpenditureCertificationImageUrls;
import com.poorlex.poorlex.expenditure.domain.ExpenditureDescription;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ExpenditureFixture {

    private ExpenditureFixture() {

    }

    public static Expenditure simpleWith(final int amount, final Long memberId, final LocalDateTime dateTime) {
        return simpleWith(amount, memberId, LocalDate.from(dateTime));
    }

    public static Expenditure simpleWith(final int amount, final Long memberId, final LocalDate date) {
        final ExpenditureAmount expenditureAmount = new ExpenditureAmount(amount);
        final ExpenditureDescription description = new ExpenditureDescription("description");
        final List<ExpenditureCertificationImageUrl> imageUrlList = List.of(
            ExpenditureCertificationImageUrl.withoutIdAndExpenditure("imageUrl1"),
            ExpenditureCertificationImageUrl.withoutIdAndExpenditure("imageUrl2")
        );
        final ExpenditureCertificationImageUrls imageUrls = new ExpenditureCertificationImageUrls(imageUrlList);

        return Expenditure.withoutId(expenditureAmount, memberId, date, description, imageUrls);
    }
}

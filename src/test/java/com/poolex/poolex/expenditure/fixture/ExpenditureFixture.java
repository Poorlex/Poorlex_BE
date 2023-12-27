package com.poolex.poolex.expenditure.fixture;

import com.poolex.poolex.expenditure.domain.Expenditure;
import com.poolex.poolex.expenditure.domain.ExpenditureAmount;
import com.poolex.poolex.expenditure.domain.ExpenditureCertificationImageUrl;
import com.poolex.poolex.expenditure.domain.ExpenditureCertificationImageUrls;
import com.poolex.poolex.expenditure.domain.ExpenditureDescription;
import java.time.LocalDateTime;
import java.util.List;

public class ExpenditureFixture {

    private ExpenditureFixture() {

    }

    public static Expenditure simpleWith(final int amount, final Long memberId, final LocalDateTime date) {
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

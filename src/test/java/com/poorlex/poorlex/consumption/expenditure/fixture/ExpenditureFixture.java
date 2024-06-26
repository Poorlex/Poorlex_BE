package com.poorlex.poorlex.consumption.expenditure.fixture;

import com.poorlex.poorlex.consumption.expenditure.domain.Expenditure;
import com.poorlex.poorlex.consumption.expenditure.domain.ExpenditureAmount;
import com.poorlex.poorlex.consumption.expenditure.domain.ExpenditureDescription;
import java.time.LocalDate;

public class ExpenditureFixture {

    private ExpenditureFixture() {

    }

    public static Expenditure simpleWithMainImage(final Long amount, final Long memberId, final LocalDate date) {
        final ExpenditureAmount expenditureAmount = new ExpenditureAmount(amount);
        final ExpenditureDescription description = new ExpenditureDescription("description");

        return Expenditure.withoutId(expenditureAmount, memberId, date, description, "mainImage", null);
    }


    public static Expenditure simpleWithMainImageAndSubImage(final Long amount,
                                                             final Long memberId,
                                                             final LocalDate date) {
        final ExpenditureAmount expenditureAmount = new ExpenditureAmount(amount);
        final ExpenditureDescription description = new ExpenditureDescription("description");

        return Expenditure.withoutId(expenditureAmount, memberId, date, description, "mainImage", "subImgaeUrl");
    }
}

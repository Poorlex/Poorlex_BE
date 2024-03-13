package com.poorlex.poorlex.expenditure.fixture;

import com.poorlex.poorlex.expenditure.domain.Expenditure;
import com.poorlex.poorlex.expenditure.domain.ExpenditureAmount;
import com.poorlex.poorlex.expenditure.domain.ExpenditureDescription;
import java.time.LocalDate;

public class ExpenditureFixture {

    private ExpenditureFixture() {

    }

    public static Expenditure simpleWithMainImage(final int amount, final Long memberId, final LocalDate date) {
        final ExpenditureAmount expenditureAmount = new ExpenditureAmount(amount);
        final ExpenditureDescription description = new ExpenditureDescription("description");

        return Expenditure.withoutId(expenditureAmount, memberId, date, description, "mainImage", null);
    }


    public static Expenditure simpleWithMainImageAndSubImage(final int amount,
                                                             final Long memberId,
                                                             final LocalDate date) {
        final ExpenditureAmount expenditureAmount = new ExpenditureAmount(amount);
        final ExpenditureDescription description = new ExpenditureDescription("description");

        return Expenditure.withoutId(expenditureAmount, memberId, date, description, "mainImage", "subImgaeUrl");
    }
}

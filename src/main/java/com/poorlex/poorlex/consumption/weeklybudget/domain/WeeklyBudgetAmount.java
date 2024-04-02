package com.poorlex.poorlex.consumption.weeklybudget.domain;

import com.poorlex.poorlex.exception.ApiException;
import com.poorlex.poorlex.exception.ExceptionTag;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WeeklyBudgetAmount {

    private static final Long MINIMUM_PRICE = 0L;
    private static final Long MAXIMUM_PRICE = 9_999_999L;
    @Column(name = "amount")
    private Long value;

    public WeeklyBudgetAmount(final Long value) {
        validate(value);
        this.value = value;
    }

    private void validate(final Long value) {
        if (MINIMUM_PRICE > value || value > MAXIMUM_PRICE) {
            final String errorMessage = String.format("주간 예산은 %d원 이상 %d원 이하여야 합니다. ( 입력 금액 : %d )",
                                                      MINIMUM_PRICE,
                                                      MAXIMUM_PRICE,
                                                      value);
            throw new ApiException(ExceptionTag.WEEKLY_BUDGET_AMOUNT, errorMessage);
        }
    }

    public Long getValue() {
        return value;
    }
}

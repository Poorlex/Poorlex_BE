package com.poorlex.poorlex.weeklybudget.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WeeklyBudgetAmount {

    private static final int MINIMUM_PRICE = 0;
    private static final int MAXIMUM_PRICE = 9_999_999;
    @Column(name = "amount")
    private int value;

    public WeeklyBudgetAmount(final int value) {
        validate(value);
        this.value = value;
    }

    private void validate(final int value) {
        if (MINIMUM_PRICE > value || value > MAXIMUM_PRICE) {
            throw new IllegalArgumentException(
                String.format("주간 예산은 %d원 이상 %d원 이하여야 합니다. ( 입력 금액 : %d )", MINIMUM_PRICE, MAXIMUM_PRICE, value)
            );
        }
    }

    public int getValue() {
        return value;
    }
}

package com.poorlex.poorlex.expenditure.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExpenditureAmount {

    private static final long MINIMUM_PRICE = 0L;
    private static final long MAXIMUM_PRICE = 9_999_999L;
    @Column(name = "amount", nullable = false)
    private long value;

    public ExpenditureAmount(final long value) {
        validate(value);
        this.value = value;
    }

    private void validate(final long value) {
        if (MINIMUM_PRICE > value || value > MAXIMUM_PRICE) {
            throw new IllegalArgumentException(
                    String.format("지출 금액은 %d원 이상 %d원 이하입니다. ( 입력 지출 금액 : %d )", MINIMUM_PRICE, MAXIMUM_PRICE, value)
            );
        }
    }

    public long getValue() {
        return value;
    }
}

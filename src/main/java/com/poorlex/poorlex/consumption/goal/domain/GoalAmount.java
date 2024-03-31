package com.poorlex.poorlex.consumption.goal.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoalAmount {

    private static final long MINIMUM_PRICE = 0L;
    private static final long MAXIMUM_PRICE = 9_999_999L;
    @Column(name = "amount")
    private long value;

    public GoalAmount(final long value) {
        validate(value);
        this.value = value;
    }

    private void validate(final long value) {
        if (MINIMUM_PRICE > value || value > MAXIMUM_PRICE) {
            throw new IllegalArgumentException();
        }
    }

    public long getValue() {
        return value;
    }
}

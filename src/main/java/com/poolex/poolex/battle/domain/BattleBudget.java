package com.poolex.poolex.battle.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BattleBudget {

    private static final int UNIT = 10000;
    private static final int MINIMUM_BUDGET = 10000;
    private static final int MAXIMUM_BUDGET = 200000;
    @Column(name = "budget")
    private int value;

    public BattleBudget(final int value) {
        validate(value);
        this.value = value;
    }

    private void validate(final int value) {
        validateRange(value);
        validateUnit(value);
    }

    private void validateRange(final int value) {
        if (value < MINIMUM_BUDGET || MAXIMUM_BUDGET < value) {
            throw new IllegalArgumentException();
        }
    }

    private void validateUnit(final int value) {
        if (value % UNIT != 0) {
            throw new IllegalArgumentException();
        }
    }

    public boolean isBetween(final BattleBudget lowerBound, final BattleBudget upperBound) {
        return lowerBound.value <= this.value && this.value <= upperBound.value;
    }

    public int getValue() {
        return value;
    }

    public BattleDifficulty getDifficulty() {
        return BattleDifficulty.findByBattleBudget(this)
                .orElseThrow(IllegalArgumentException::new);
    }
}

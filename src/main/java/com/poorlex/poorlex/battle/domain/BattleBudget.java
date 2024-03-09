package com.poorlex.poorlex.battle.domain;

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
    @Column(name = "budget", updatable = false, nullable = false)
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
            throw new IllegalArgumentException(
                    String.format("예산은 %d 이상 %d 이하여야 합니다. ( 입력값 : %d )", MINIMUM_BUDGET, MAXIMUM_BUDGET, value)
            );
        }
    }

    private void validateUnit(final int value) {
        if (value % UNIT != 0) {
            throw new IllegalArgumentException(
                    String.format("예산은 %d 단위여야 합니다. ( 입력값 : %d )", UNIT, value)
            );
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

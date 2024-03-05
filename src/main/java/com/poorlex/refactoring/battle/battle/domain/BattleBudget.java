package com.poorlex.refactoring.battle.battle.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BattleBudget {

    @Column(name = "budget", updatable = false, nullable = false)
    private Long value;

    BattleBudget(final Long value) {
        this.value = value;
    }

    public boolean isBetween(final BattleBudget lowerBound, final BattleBudget upperBound) {
        return lowerBound.value <= this.value && this.value <= upperBound.value;
    }

    public BattleDifficulty getDifficulty() {
        return BattleDifficulty.findByBattleBudget(this)
            .orElseThrow(IllegalArgumentException::new);
    }

    public Long getValue() {
        return value;
    }
}

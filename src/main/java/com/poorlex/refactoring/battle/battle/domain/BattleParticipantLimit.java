package com.poorlex.refactoring.battle.battle.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BattleParticipantLimit {

    @Column(name = "max_size", updatable = false, nullable = false)
    private int value;

    BattleParticipantLimit(final int value) {
        this.value = value;
    }

    public boolean isBetween(final BattleParticipantLimit lowerBound, final BattleParticipantLimit upperBound) {
        return lowerBound.value <= this.value && this.value <= upperBound.value;
    }

    public BattleSizeType getBattleSizeType() {
        return BattleSizeType.findByParticipantSize(this)
            .orElseThrow(IllegalArgumentException::new);
    }

    public int getValue() {
        return value;
    }

    public boolean hasSameOrGreaterValue(final int target) {
        return hasSameValue(target) || hasGreaterValue(target);
    }

    public boolean hasSameValue(final int target) {
        return value == target;
    }

    public boolean hasGreaterValue(final int target) {
        return value > target;
    }
}

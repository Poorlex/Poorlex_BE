package com.poolex.poolex.battle.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BattleParticipantSize {

    private static final int MINIMUM_SIZE = 1;
    private static final int MAXIMUM_SIZE = 10;
    @Column(name = "max_size", updatable = false, nullable = false)
    private int value;

    public BattleParticipantSize(final int value) {
        validate(value);
        this.value = value;
    }

    private void validate(final int value) {
        validateRange(value);
    }

    private void validateRange(final int value) {
        if (value < MINIMUM_SIZE || value > MAXIMUM_SIZE) {
            throw new IllegalArgumentException();
        }
    }

    public boolean isBetween(final BattleParticipantSize lowerBound, final BattleParticipantSize upperBound) {
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

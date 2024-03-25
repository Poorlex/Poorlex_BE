package com.poorlex.poorlex.battle.domain;

import com.poorlex.poorlex.exception.ApiException;
import com.poorlex.poorlex.exception.ExceptionTag;
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
            final String errorMessage = String.format("배틀 인원의 %d 이상 %d 이하여야 합니다. ( 입력값 : %d )",
                                                      MINIMUM_SIZE,
                                                      MAXIMUM_SIZE,
                                                      value);
            throw new ApiException(ExceptionTag.BATTLE_PARTICIPANT_SIZE, errorMessage);
        }
    }

    public boolean isBetween(final BattleParticipantSize lowerBound, final BattleParticipantSize upperBound) {
        return lowerBound.value <= this.value && this.value <= upperBound.value;
    }

    public BattleType getBattleSizeType() {
        return BattleType.findByParticipantSize(this)
                .orElseThrow(() -> {
                    final String errorMessage = String.format("배틀 인원 수에 맞는 배틀 타입이 존재하지 않습니다. ( 인원 수  : %d )", value);
                    throw new ApiException(ExceptionTag.BATTLE_TYPE, errorMessage);
                });
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

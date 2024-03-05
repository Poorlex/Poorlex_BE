package com.poorlex.refactoring.user.point.domain;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public enum MemberLevel {
    LEVEL_5(1440, 5, point -> 0),
    LEVEL_4(600, 4, point -> LEVEL_5.lowerBound - point),
    LEVEL_3(190, 3, point -> LEVEL_4.lowerBound - point),
    LEVEL_2(70, 2, point -> LEVEL_3.lowerBound - point),
    LEVEL_1(0, 1, point -> LEVEL_2.lowerBound - point);

    private final int lowerBound;
    private final int number;
    private final Function<Integer, Integer> calculatePointForNextLevelFunction;

    MemberLevel(final int lowerBound,
                final int number,
                final UnaryOperator<Integer> calculatePointForNextLevelFunction) {
        this.lowerBound = lowerBound;
        this.number = number;
        this.calculatePointForNextLevelFunction = calculatePointForNextLevelFunction;
    }

    public static Optional<MemberLevel> findByPoint(final Point memberPoint) {
        return Arrays.stream(values())
            .filter(level -> memberPoint.isGreaterOrEqualThan(level.lowerBound))
            .findFirst();
    }

    public int getLevelRange() {
        if (this == LEVEL_5) {
            return 0;
        }
        final MemberLevel nextLevel = values()[ordinal() - 1];
        return nextLevel.lowerBound - this.lowerBound;
    }

    public int getNumber() {
        return number;
    }

    public int getLowerBound() {
        return lowerBound;
    }

    public Integer getGetPointForNextLevel(final Integer point) {
        return calculatePointForNextLevelFunction.apply(point);
    }
}

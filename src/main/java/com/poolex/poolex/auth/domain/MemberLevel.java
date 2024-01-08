package com.poolex.poolex.auth.domain;

import com.poolex.poolex.point.domain.Point;
import java.util.Arrays;
import java.util.Optional;

public enum MemberLevel {
    LEVEL_5(1440, 5),
    LEVEL_4(600, 4),
    LEVEL_3(190, 3),
    LEVEL_2(70, 2),
    LEVEL_1(0, 1);

    private final int lowerBound;
    private final int number;

    MemberLevel(final int lowerBound, final int number) {
        this.lowerBound = lowerBound;
        this.number = number;
    }

    public static Optional<MemberLevel> findByPoint(final Point memberPoint) {
        return Arrays.stream(values())
            .filter(level -> memberPoint.isGreaterOrEqualThan(level.lowerBound))
            .findFirst();
    }

    public int getNumber() {
        return number;
    }
}

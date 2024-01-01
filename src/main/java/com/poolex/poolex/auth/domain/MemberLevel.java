package com.poolex.poolex.auth.domain;

import com.poolex.poolex.point.domain.Point;
import java.util.Arrays;
import java.util.Optional;

public enum MemberLevel {
    LEVEL_5(1440),
    LEVEL_4(600),
    LEVEL_3(190),
    LEVEL_2(70),
    LEVEL_1(0);

    private final int lowerBound;

    MemberLevel(final int lowerBound) {
        this.lowerBound = lowerBound;
    }

    public static Optional<MemberLevel> findByPoint(final Point memberPoint) {
        return Arrays.stream(values())
            .filter(level -> memberPoint.isGreaterOrEqualThan(level.lowerBound))
            .findFirst();
    }
}

package com.poolex.poolex.login.domain;

import java.util.Arrays;
import java.util.Optional;

public enum MemberLevel {
    LEVEL_5(new MemberPoint(1440)),
    LEVEL_4(new MemberPoint(600)),
    LEVEL_3(new MemberPoint(190)),
    LEVEL_2(new MemberPoint(70)),
    LEVEL_1(new MemberPoint(0));

    private final MemberPoint lowerBound;

    MemberLevel(final MemberPoint lowerBound) {
        this.lowerBound = lowerBound;
    }

    public static Optional<MemberLevel> findByMemberPoint(final MemberPoint memberPoint) {
        return Arrays.stream(values())
            .filter(level -> memberPoint.isGreaterOrEqualThan(level.lowerBound))
            .findFirst();
    }
}

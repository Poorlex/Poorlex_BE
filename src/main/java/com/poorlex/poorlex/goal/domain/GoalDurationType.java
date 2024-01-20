package com.poorlex.poorlex.goal.domain;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

public enum GoalDurationType {
    SHORT((long) LocalDateTime.MAX.getDayOfYear() - 1),
    MIDDLE((long) LocalDateTime.MAX.getDayOfYear() * 5 - 1),
    LONG(Long.MAX_VALUE);

    private final long maximumDays;

    GoalDurationType(final long maximumDays) {
        this.maximumDays = maximumDays;
    }

    public static Optional<GoalDurationType> findByDays(final long targetDays) {
        return Arrays.stream(values())
            .filter(type -> type.maximumDays >= targetDays)
            .findFirst();
    }
}

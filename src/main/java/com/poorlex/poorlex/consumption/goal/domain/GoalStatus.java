package com.poorlex.poorlex.consumption.goal.domain;

import java.util.Arrays;
import java.util.Optional;

public enum GoalStatus {
    PROGRESS,
    FINISH;

    public static Optional<GoalStatus> findByName(final String name) {
        return Arrays.stream(values())
                .filter(status -> status.name().equalsIgnoreCase(name.toLowerCase()))
                .findFirst();
    }
}

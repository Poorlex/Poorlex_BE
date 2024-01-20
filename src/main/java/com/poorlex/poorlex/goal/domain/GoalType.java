package com.poorlex.poorlex.goal.domain;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public enum GoalType {
    STABLE_FUTURE(List.of()),
    WEALTH_AND_HONOR(List.of()),
    STRESS_RESOLVE(List.of()),
    SUCCESSFUL_AT_WORK(List.of()),
    REST_AND_REFRESH(List.of());
    private final List<GoalName> recommends;

    GoalType(final List<GoalName> recommends) {
        this.recommends = recommends;
    }

    public List<GoalName> getRecommends() {
        return recommends;
    }

    public static Optional<GoalType> findByName(final String name) {
        return Arrays.stream(values())
            .filter(type -> type.name().equals(name))
            .findFirst();
    }
}

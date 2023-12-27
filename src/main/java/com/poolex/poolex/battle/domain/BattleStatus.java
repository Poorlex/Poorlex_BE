package com.poolex.poolex.battle.domain;

import java.util.Arrays;

public enum BattleStatus {
    RECRUITING,
    RECRUITING_FINISHED,
    PROGRESS,
    COMPLETE;

    public static BattleStatus findByName(final String statusName) {
        return Arrays.stream(values())
            .filter(status -> status.name().equals(statusName.toUpperCase()))
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }
}

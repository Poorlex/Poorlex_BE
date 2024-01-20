package com.poorlex.poorlex.battle.domain;

import java.util.Arrays;
import java.util.List;

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

    public static List<BattleStatus> getReadiedStatues() {
        return List.of(BattleStatus.RECRUITING, BattleStatus.RECRUITING_FINISHED);
    }
}

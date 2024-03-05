package com.poorlex.refactoring.battle.history.domain;

import java.util.Arrays;

public enum BattleDifficulty {
    EASY,
    NORMAL,
    HARD;

    public static BattleDifficulty findByName(final String name) {
        return Arrays.stream(values())
            .filter(battleDifficulty -> battleDifficulty.name().equalsIgnoreCase(name))
            .findFirst()
            .orElseThrow(IllegalAccessError::new);
    }
}

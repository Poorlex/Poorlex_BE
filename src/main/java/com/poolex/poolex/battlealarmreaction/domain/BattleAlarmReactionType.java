package com.poolex.poolex.battlealarmreaction.domain;

import java.util.Arrays;

public enum BattleAlarmReactionType {
    PRAISE,
    SCOLD;

    public static BattleAlarmReactionType findByName(final String name) {
        return Arrays.stream(values())
            .filter(reactionType -> reactionType.name().equals(name.toUpperCase()))
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }
}

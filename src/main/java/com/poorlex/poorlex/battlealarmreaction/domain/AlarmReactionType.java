package com.poorlex.poorlex.battlealarmreaction.domain;

import java.util.Arrays;

public enum AlarmReactionType {
    PRAISE,
    SCOLD;

    public static AlarmReactionType findByName(final String name) {
        return Arrays.stream(values())
            .filter(reactionType -> reactionType.name().equals(name.toUpperCase()))
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }
}

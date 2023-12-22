package com.poolex.poolex.battle.domain;

import java.util.Arrays;
import java.util.Optional;

public enum BattleSizeType {
    SMALL(new BattleParticipantSize(1), new BattleParticipantSize(4)),
    LARGE(new BattleParticipantSize(5), new BattleParticipantSize(10));
    private final BattleParticipantSize minimumSize;
    private final BattleParticipantSize maximumSize;

    BattleSizeType(final BattleParticipantSize minimumSize, final BattleParticipantSize maximumSize) {
        this.minimumSize = minimumSize;
        this.maximumSize = maximumSize;
    }

    public static Optional<BattleSizeType> findByParticipantSize(final BattleParticipantSize battleParticipantSize) {
        return Arrays.stream(values())
                .filter(size -> battleParticipantSize.isBetween(size.minimumSize, size.maximumSize))
                .findAny();
    }
}

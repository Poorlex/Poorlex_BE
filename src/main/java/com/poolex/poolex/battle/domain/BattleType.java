package com.poolex.poolex.battle.domain;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

public enum BattleType {
    SMALL(
        new BattleParticipantSize(1),
        new BattleParticipantSize(5),
        Map.of(
            1, 20,
            2, 10
        )
    ),
    LARGE(
        new BattleParticipantSize(6),
        new BattleParticipantSize(10),
        Map.of(
            1, 30,
            2, 20,
            3, 10
        )
    );
    private final BattleParticipantSize minimumSize;
    private final BattleParticipantSize maximumSize;
    private final Map<Integer, Integer> scoresForRank;

    BattleType(final BattleParticipantSize minimumSize,
               final BattleParticipantSize maximumSize,
               final Map<Integer, Integer> scoresForRank) {
        this.minimumSize = minimumSize;
        this.maximumSize = maximumSize;
        this.scoresForRank = scoresForRank;
    }

    public static Optional<BattleType> findByParticipantSize(final BattleParticipantSize battleParticipantSize) {
        return Arrays.stream(values())
            .filter(size -> battleParticipantSize.isBetween(size.minimumSize, size.maximumSize))
            .findAny();
    }

    public int getScore(final int rank) {
        if (!scoresForRank.containsKey(rank)) {
            return 0;
        }
        return scoresForRank.get(rank);
    }
}

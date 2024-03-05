package com.poorlex.refactoring.battle.battle.domain;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

public enum BattleSizeType {
    SMALL(
        new BattleParticipantLimit(1),
        new BattleParticipantLimit(5),
        Map.of(
            1, 20,
            2, 10
        )
    ),
    LARGE(
        new BattleParticipantLimit(6),
        new BattleParticipantLimit(10),
        Map.of(
            1, 30,
            2, 20,
            3, 10
        )
    );
    private final BattleParticipantLimit minimumSize;
    private final BattleParticipantLimit maximumSize;
    private final Map<Integer, Integer> scoresForRank;

    BattleSizeType(final BattleParticipantLimit minimumSize,
                   final BattleParticipantLimit maximumSize,
                   final Map<Integer, Integer> scoresForRank) {
        this.minimumSize = minimumSize;
        this.maximumSize = maximumSize;
        this.scoresForRank = scoresForRank;
    }

    public static Optional<BattleSizeType> findByParticipantSize(final BattleParticipantLimit battleParticipantLimit) {
        return Arrays.stream(values())
            .filter(size -> battleParticipantLimit.isBetween(size.minimumSize, size.maximumSize))
            .findAny();
    }

    public int getScore(final int rank) {
        if (!scoresForRank.containsKey(rank)) {
            return 0;
        }
        return scoresForRank.get(rank);
    }
}

package com.poolex.poolex.battle.domain;

import java.util.Arrays;
import java.util.Optional;

public enum BattleDifficulty {
    EASY(new BattleBudget(150000), new BattleBudget(200000)),
    NORMAL(new BattleBudget(90000), new BattleBudget(140000)),
    HARD(new BattleBudget(10000), new BattleBudget(80000));
    private final BattleBudget minimumBudget;
    private final BattleBudget maximumBudget;

    BattleDifficulty(final BattleBudget minimumBudget, final BattleBudget maximumBudget) {
        this.minimumBudget = minimumBudget;
        this.maximumBudget = maximumBudget;
    }

    public static Optional<BattleDifficulty> findByBattleBudget(final BattleBudget budget) {
        return Arrays.stream(values())
            .filter(difficulty -> budget.isBetween(difficulty.minimumBudget, difficulty.maximumBudget))
            .findFirst();
    }
}

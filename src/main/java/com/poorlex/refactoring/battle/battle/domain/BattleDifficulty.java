package com.poorlex.refactoring.battle.battle.domain;

import java.util.Arrays;
import java.util.Optional;

public enum BattleDifficulty {
    EASY(new BattleBudget(150000L), new BattleBudget(200000L)),
    NORMAL(new BattleBudget(90000L), new BattleBudget(140000L)),
    HARD(new BattleBudget(10000L), new BattleBudget(80000L));
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

package com.poorlex.refactoring.battle.history.domain.dto;

import com.poorlex.refactoring.battle.battle.domain.BattleDifficulty;

public interface BattlDifficultySuccessCountDto {

    int getSuccessCount();

    BattleDifficulty getDifficulty();
}

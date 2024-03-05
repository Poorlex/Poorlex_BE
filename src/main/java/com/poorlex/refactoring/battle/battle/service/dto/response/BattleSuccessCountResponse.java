package com.poorlex.refactoring.battle.battle.service.dto.response;

import com.poorlex.refactoring.battle.battle.domain.BattleDifficulty;
import com.poorlex.refactoring.battle.history.domain.dto.BattlDifficultySuccessCountDto;
import java.util.List;

public class BattleSuccessCountResponse {

    private int totalBattleSuccessCount = 0;
    private int hardBattleSuccessCount = 0;
    private int normalBattleSuccessCount = 0;
    private int easyBattleSuccessCount = 0;

    public BattleSuccessCountResponse(final List<BattlDifficultySuccessCountDto> battleDifficultySuccessCount) {
        battleDifficultySuccessCount.stream()
            .forEach(this::setDifficultySuccessCount);

        this.totalBattleSuccessCount = easyBattleSuccessCount + normalBattleSuccessCount + hardBattleSuccessCount;
    }

    private void setDifficultySuccessCount(final BattlDifficultySuccessCountDto battleSuccessCountGroup) {
        final BattleDifficulty difficulty = battleSuccessCountGroup.getDifficulty();
        final int successCount = battleSuccessCountGroup.getSuccessCount();

        if (difficulty == BattleDifficulty.HARD) {
            this.hardBattleSuccessCount = successCount;
            return;
        }

        if (difficulty == BattleDifficulty.NORMAL) {
            this.normalBattleSuccessCount = successCount;
            return;
        }

        this.normalBattleSuccessCount = successCount;
    }

    public int getTotalBattleSuccessCount() {
        return totalBattleSuccessCount;
    }

    public int getHardBattleSuccessCount() {
        return hardBattleSuccessCount;
    }

    public int getNormalBattleSuccessCount() {
        return normalBattleSuccessCount;
    }

    public int getEasyBattleSuccessCount() {
        return easyBattleSuccessCount;
    }
}

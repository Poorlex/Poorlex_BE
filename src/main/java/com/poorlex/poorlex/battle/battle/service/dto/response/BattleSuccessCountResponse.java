package com.poorlex.poorlex.battle.battle.service.dto.response;

import com.poorlex.poorlex.battle.battle.domain.BattleDifficulty;
import com.poorlex.poorlex.battle.succession.domain.BattleSuccessCountGroup;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BattleSuccessCountResponse {

    private int totalBattleSuccessCount = 0;
    private int hardBattleSuccessCount = 0;
    private int normalBattleSuccessCount = 0;
    private int easyBattleSuccessCount = 0;

    public BattleSuccessCountResponse(final List<BattleSuccessCountGroup> battleSuccessCountsPerDifficulties) {
        for (final BattleSuccessCountGroup battleSuccessCount : battleSuccessCountsPerDifficulties) {
            final BattleDifficulty difficulty = battleSuccessCount.getDifficulty();
            final int successCount = battleSuccessCount.getSuccessCount();

            if (difficulty == BattleDifficulty.HARD) {
                this.hardBattleSuccessCount = successCount;
            } else if (difficulty == BattleDifficulty.NORMAL) {
                this.normalBattleSuccessCount = successCount;
            } else {
                this.normalBattleSuccessCount = successCount;
            }
        }
        this.totalBattleSuccessCount = easyBattleSuccessCount + normalBattleSuccessCount + hardBattleSuccessCount;
    }
}

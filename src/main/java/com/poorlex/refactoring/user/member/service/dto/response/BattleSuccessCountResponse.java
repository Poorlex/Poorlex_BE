package com.poorlex.refactoring.user.member.service.dto.response;

import com.poorlex.refactoring.user.member.service.dto.BattleDifficultySuccessCountDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BattleSuccessCountResponse {

    private final int totalBattleSuccessCount;
    private final int easyBattleSuccessCount;
    private final int normalBattleSuccessCount;
    private final int hardBattleSuccessCount;

    public static BattleSuccessCountResponse from(final BattleDifficultySuccessCountDto difficultySuccessCount) {
        final int easyBattleSuccessCount = difficultySuccessCount.getEasyBattleSuccessCount();
        final int normalBattleSuccessCount = difficultySuccessCount.getNormalBattleSuccessCount();
        final int hardBattleSuccessCount = difficultySuccessCount.getHardBattleSuccessCount();

        return new BattleSuccessCountResponse(
            easyBattleSuccessCount + normalBattleSuccessCount + hardBattleSuccessCount,
            easyBattleSuccessCount,
            normalBattleSuccessCount,
            hardBattleSuccessCount
        );
    }
}

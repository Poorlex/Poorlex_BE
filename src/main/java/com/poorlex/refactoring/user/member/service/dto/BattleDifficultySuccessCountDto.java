package com.poorlex.refactoring.user.member.service.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BattleDifficultySuccessCountDto {

    private final int easyBattleSuccessCount;
    private final int normalBattleSuccessCount;
    private final int hardBattleSuccessCount;
}

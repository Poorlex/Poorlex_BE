package com.poorlex.refactoring.battle.battle.service.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BattleParticipantNicknameAndLevelDto {

    private final String nickname;
    private final int level;

}

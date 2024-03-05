package com.poorlex.refactoring.battle.battle.service.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BattleParticipantDto {

    private final Long memberId;
    private final String role;

}

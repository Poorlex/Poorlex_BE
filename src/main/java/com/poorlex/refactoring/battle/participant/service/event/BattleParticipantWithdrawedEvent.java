package com.poorlex.refactoring.battle.participant.service.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BattleParticipantWithdrawedEvent {

    private final Long battleId;
}

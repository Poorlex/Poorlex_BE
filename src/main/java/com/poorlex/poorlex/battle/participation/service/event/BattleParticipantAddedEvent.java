package com.poorlex.poorlex.battle.participation.service.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BattleParticipantAddedEvent {

    private final Long battleId;
}

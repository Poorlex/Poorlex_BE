package com.poorlex.refactoring.battle.invitation.service.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BattleInviteAcceptedEvent {

    private final Long invitorBattleParticipantId;
    private final Long invitorMemberId;
    private final Long invitedMemberId;
}

package com.poorlex.poorlex.battle.invitation.service.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BattleInvitedEvent {

    private final Long inviteBattleParticipantId;
    private final Long invitedMemberId;
}

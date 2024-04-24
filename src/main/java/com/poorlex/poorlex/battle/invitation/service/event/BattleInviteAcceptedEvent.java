package com.poorlex.poorlex.battle.invitation.service.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BattleInviteAcceptedEvent {

    private final Long inviteBattleParticipantId;
    private final Long inviteMemberId;
    private final Long invitedMemberId;
}

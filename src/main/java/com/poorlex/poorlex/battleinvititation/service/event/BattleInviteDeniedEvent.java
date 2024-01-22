package com.poorlex.poorlex.battleinvititation.service.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BattleInviteDeniedEvent {

    private final Long inviteBattleParticipantId;
    private final Long invitedMemberId;
}

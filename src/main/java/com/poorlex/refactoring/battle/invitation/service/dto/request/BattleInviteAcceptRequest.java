package com.poorlex.refactoring.battle.invitation.service.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BattleInviteAcceptRequest {

    private Long invitorBattleParticipantId;
}

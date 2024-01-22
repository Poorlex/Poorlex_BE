package com.poorlex.poorlex.battleinvititation.service.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BattleInviteDenyRequest {

    private Long inviteBattleParticipantId;
}

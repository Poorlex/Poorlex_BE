package com.poorlex.refactoring.battle.invitation.service.provider;

public interface BattleParticipantIdProvider {

    Long byBattleIdAndMemberId(final Long battleId, final Long memberId);
}

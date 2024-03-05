package com.poorlex.refactoring.battle.invitation.service.provider;

public interface BattleParticipantExistenceProvider {

    boolean isExistByBattleIdAndMemberId(final Long battleId, final Long memberId);

    boolean isExistByBattleParticipantId(final Long battleParticipantId);
}

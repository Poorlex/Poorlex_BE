package com.poorlex.refactoring.battle.notification.service.provider;

public interface BattleParticipantHasManagerRoleProvider {

    boolean byBattleIdAndMemberId(final Long battleId, final Long memberId);
}

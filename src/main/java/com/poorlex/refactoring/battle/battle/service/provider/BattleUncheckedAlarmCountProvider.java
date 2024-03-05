package com.poorlex.refactoring.battle.battle.service.provider;

public interface BattleUncheckedAlarmCountProvider {

    int getByBattleIdAndMemberId(final Long battleId, final Long memberId);
}

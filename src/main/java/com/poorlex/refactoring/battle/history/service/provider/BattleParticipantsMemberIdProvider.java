package com.poorlex.refactoring.battle.history.service.provider;

import java.util.List;

public interface BattleParticipantsMemberIdProvider {

    List<Long> byBattleId(final Long battleId);
}

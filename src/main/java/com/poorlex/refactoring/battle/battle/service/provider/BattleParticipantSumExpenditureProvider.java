package com.poorlex.refactoring.battle.battle.service.provider;

import java.time.LocalDateTime;

public interface BattleParticipantSumExpenditureProvider {

    Long byMemberIdBetween(final Long memberId,
                           final LocalDateTime battleStart,
                           final LocalDateTime battleEnd);

}

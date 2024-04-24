package com.poorlex.poorlex.battle.battle.domain;

import com.poorlex.poorlex.battle.participation.domain.BattleParticipant;

public interface BattleParticipantWithExpenditure {

    BattleParticipant getBattleParticipant();

    Long getExpenditure();

    Long getExpenditureCount();
}

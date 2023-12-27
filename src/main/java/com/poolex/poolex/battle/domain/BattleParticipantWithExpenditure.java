package com.poolex.poolex.battle.domain;

import com.poolex.poolex.participate.domain.BattleParticipant;

public interface BattleParticipantWithExpenditure {

    BattleParticipant getBattleParticipant();

    int getExpenditure();
}

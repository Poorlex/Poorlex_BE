package com.poorlex.poorlex.battle.domain;

import com.poorlex.poorlex.participate.domain.BattleParticipant;

public interface BattleParticipantWithExpenditure {

    BattleParticipant getBattleParticipant();

    Long getExpenditure();
}

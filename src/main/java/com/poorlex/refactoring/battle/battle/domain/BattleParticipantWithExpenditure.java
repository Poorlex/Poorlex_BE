package com.poorlex.refactoring.battle.battle.domain;

import com.poorlex.refactoring.battle.participant.domain.BattleParticipant;

public interface BattleParticipantWithExpenditure {

    BattleParticipant getBattleParticipant();

    int getExpenditure();
}

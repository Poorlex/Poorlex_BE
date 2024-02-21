package com.poorlex.poorlex.batch.config;

import com.poorlex.poorlex.battle.domain.Battle;
import com.poorlex.poorlex.participate.domain.BattleParticipant;

public class BattleParticipantWithBattleSumExpenditure {

    private final BattleParticipant battleParticipant;
    private final Battle battle;
    private final Long sumExpenditure;

    public BattleParticipantWithBattleSumExpenditure(final BattleParticipant battleParticipant,
                                                     final Battle battle,
                                                     final Long sumExpenditure) {
        this.battleParticipant = battleParticipant;
        this.battle = battle;
        this.sumExpenditure = sumExpenditure;
    }

    public BattleParticipant getBattleParticipant() {
        return battleParticipant;
    }

    public Battle getBattle() {
        return battle;
    }

    public Long getSumExpenditure() {
        return sumExpenditure;
    }
}

package com.poorlex.refactoring.battle.history.service.provider;

public interface BattleSuccessPointProvider {

    int getPointBy(final Long battleId, final int rank);
}

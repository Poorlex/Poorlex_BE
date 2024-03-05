package com.poorlex.refactoring.battle.alarm.service.dto;

public class MemberBattleIdAndBudgetDto {

    private final Long battleId;
    private final int budget;

    public MemberBattleIdAndBudgetDto(final Long battleId, final int budget) {
        this.battleId = battleId;
        this.budget = budget;
    }

    public Long getBattleId() {
        return battleId;
    }

    public int getBudget() {
        return budget;
    }
}

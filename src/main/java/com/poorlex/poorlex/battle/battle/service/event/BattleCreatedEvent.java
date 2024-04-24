package com.poorlex.poorlex.battle.battle.service.event;

public class BattleCreatedEvent {

    private final Long battleId;
    private final Long managerId;

    public BattleCreatedEvent(final Long battleId, final Long managerId) {
        this.battleId = battleId;
        this.managerId = managerId;
    }

    public Long getBattleId() {
        return battleId;
    }

    public Long getManagerId() {
        return managerId;
    }
}

package com.poorlex.refactoring.battle.alarm.service.dto.response;

public class UncheckedBattleAlarmCountResponse {

    private final int count;

    public UncheckedBattleAlarmCountResponse(final int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}

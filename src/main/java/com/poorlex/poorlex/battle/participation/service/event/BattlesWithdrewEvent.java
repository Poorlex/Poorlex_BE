package com.poorlex.poorlex.battle.participation.service.event;

import java.util.ArrayList;
import java.util.List;

public class BattlesWithdrewEvent {

    private final List<Long> battleIds;

    public BattlesWithdrewEvent(final List<Long> battleIds) {
        this.battleIds = battleIds;
    }

    public List<Long> getBattleIds() {
        return new ArrayList<>(battleIds);
    }
}

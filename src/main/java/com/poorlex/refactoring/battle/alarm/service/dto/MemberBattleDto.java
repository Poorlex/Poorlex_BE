package com.poorlex.refactoring.battle.alarm.service.dto;

import java.time.LocalDateTime;

public class MemberBattleDto {

    private final Long battleId;
    private final Long budget;
    private final LocalDateTime start;
    private final LocalDateTime end;

    public MemberBattleDto(final Long battleId,
                           final Long budget,
                           final LocalDateTime start,
                           final LocalDateTime end) {
        this.battleId = battleId;
        this.budget = budget;
        this.start = start;
        this.end = end;
    }

    public Long getBattleId() {
        return battleId;
    }

    public Long getBudget() {
        return budget;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }
}

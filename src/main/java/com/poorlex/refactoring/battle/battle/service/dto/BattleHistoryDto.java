package com.poorlex.refactoring.battle.battle.service.dto;

import com.poorlex.refactoring.battle.history.domain.BattleDifficulty;
import java.time.LocalDate;

public class BattleHistoryDto {

    private final String battleName;
    private final String battleImageUrl;
    private final Long memberBudgetLeft;
    private final int memberBattleRank;
    private final int battleMaxParticipantSize;
    private final int memberEarnedPoint;
    private final LocalDate battleEnd;
    private final BattleDifficulty battleDifficulty;

    public BattleHistoryDto(final String battleName,
                            final String battleImageUrl,
                            final Long memberBudgetLeft,
                            final int memberBattleRank,
                            final int battleMaxParticipantSize,
                            final int memberEarnedPoint,
                            final LocalDate battleEnd,
                            final BattleDifficulty battleDifficulty) {
        this.battleName = battleName;
        this.battleImageUrl = battleImageUrl;
        this.memberBudgetLeft = memberBudgetLeft;
        this.memberBattleRank = memberBattleRank;
        this.battleMaxParticipantSize = battleMaxParticipantSize;
        this.memberEarnedPoint = memberEarnedPoint;
        this.battleEnd = battleEnd;
        this.battleDifficulty = battleDifficulty;
    }

    public String getBattleName() {
        return battleName;
    }

    public String getBattleImageUrl() {
        return battleImageUrl;
    }

    public Long getMemberBudgetLeft() {
        return memberBudgetLeft;
    }

    public int getMemberBattleRank() {
        return memberBattleRank;
    }

    public int getBattleMaxParticipantSize() {
        return battleMaxParticipantSize;
    }

    public int getMemberEarnedPoint() {
        return memberEarnedPoint;
    }

    public LocalDate getBattleEnd() {
        return battleEnd;
    }

    public BattleDifficulty getBattleDifficulty() {
        return battleDifficulty;
    }
}

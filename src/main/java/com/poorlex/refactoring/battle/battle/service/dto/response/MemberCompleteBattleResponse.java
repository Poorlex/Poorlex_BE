package com.poorlex.refactoring.battle.battle.service.dto.response;

import com.poorlex.refactoring.battle.battle.service.dto.BattleHistoryDto;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class MemberCompleteBattleResponse implements BattleResponse {

    private final String name;
    private final String imageUrl;
    private final String difficulty;
    private final long pastDay;
    private final Long budgetLeft;
    private final int currentParticipantRank;
    private final int battleParticipantCount;
    private final int earnedPoint;

    public static MemberCompleteBattleResponse from(final BattleHistoryDto battleHistory, final LocalDate date) {
        return new MemberCompleteBattleResponse(
            battleHistory.getBattleName(),
            battleHistory.getBattleImageUrl(),
            battleHistory.getBattleDifficulty().name(),
            ChronoUnit.DAYS.between(LocalDate.from(battleHistory.getBattleEnd()), date),
            battleHistory.getMemberBudgetLeft(),
            battleHistory.getMemberBattleRank(),
            battleHistory.getBattleMaxParticipantSize(),
            battleHistory.getMemberEarnedPoint()
        );
    }
}

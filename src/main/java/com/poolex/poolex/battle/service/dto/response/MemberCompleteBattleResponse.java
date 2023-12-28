package com.poolex.poolex.battle.service.dto.response;

import com.poolex.poolex.battle.domain.Battle;
import com.poolex.poolex.battle.domain.BattleWithMemberExpenditure;
import java.time.LocalDate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class MemberCompleteBattleResponse {

    private final String name;
    private final String imageUrl;
    private final String difficulty;
    private final long pastDay;
    private final int budgetLeft;
    private final int currentParticipantRank;
    private final int maxParticipantCount;
    private final int earnedPoint;

    public static MemberCompleteBattleResponse from(final BattleWithMemberExpenditure battleInfo,
                                                    final LocalDate current,
                                                    final int currentParticipantRank) {
        final Battle battle = battleInfo.getBattle();

        return new MemberCompleteBattleResponse(
            battle.getName(),
            battle.getImageUrl(),
            battle.getDifficulty().name(),
            battle.getPastDay(current),
            battle.getBudgetLeft(battleInfo.getExpenditure()),
            currentParticipantRank,
            battle.getMaxParticipantSize().getValue(),
            battle.getBattleType().getScore(currentParticipantRank)
        );
    }
}

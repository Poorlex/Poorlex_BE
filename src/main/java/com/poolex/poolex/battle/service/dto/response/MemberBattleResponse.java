package com.poolex.poolex.battle.service.dto.response;

import com.poolex.poolex.battle.domain.Battle;
import com.poolex.poolex.battle.domain.BattleWithMemberExpenditure;
import java.time.LocalDate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class MemberBattleResponse {

    private final String name;
    private final String imageUrl;
    private final String difficulty;
    private final long dDay;
    private final int budgetLeft;
    private final int currentParticipantRank;
    private final int maxParticipantCount;

    public static MemberBattleResponse from(final BattleWithMemberExpenditure battleInfo,
                                            final int currentParticipantRank) {
        final Battle battle = battleInfo.getBattle();

        return new MemberBattleResponse(
            battle.getName(),
            battle.getImageUrl(),
            battle.getDifficulty().name(),
            battle.getDDay(LocalDate.now()),
            battle.getBudgetLeft(battleInfo.getExpenditure()),
            currentParticipantRank,
            battle.getMaxParticipantSize().getValue()
        );
    }
}

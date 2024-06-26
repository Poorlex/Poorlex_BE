package com.poorlex.poorlex.battle.battle.service.dto.response;

import com.poorlex.poorlex.battle.battle.domain.Battle;
import com.poorlex.poorlex.battle.battle.domain.BattleWithMemberExpenditure;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class MemberProgressBattleResponse {

    private final Long battleId;
    private final String name;
    private final String imageUrl;
    private final String difficulty;
    private final long dDay;
    private final int budget;
    private final int budgetLeft;
    private final int currentParticipantRank;
    private final int battleParticipantCount;
    private final int uncheckedAlarmCount;

    public static MemberProgressBattleResponse from(final BattleWithMemberExpenditure battleInfo,
                                                    final long dDay,
                                                    final int currentParticipantRank,
                                                    final int battleParticipantCount,
                                                    final int uncheckedAlarmCount) {
        final Battle battle = battleInfo.getBattle();

        return new MemberProgressBattleResponse(
            battle.getId(),
            battle.getName(),
            battle.getImageUrl(),
            battle.getDifficulty().name(),
            dDay,
            battle.getBudget(),
            battle.getBudgetLeft(battleInfo.getExpenditure()),
            currentParticipantRank,
            battleParticipantCount,
            uncheckedAlarmCount
        );
    }
}

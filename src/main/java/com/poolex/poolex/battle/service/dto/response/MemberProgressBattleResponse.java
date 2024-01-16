package com.poolex.poolex.battle.service.dto.response;

import com.poolex.poolex.battle.domain.Battle;
import com.poolex.poolex.battle.domain.BattleWithMemberExpenditure;
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
            battle.getBudgetLeft(battleInfo.getExpenditure()),
            currentParticipantRank,
            battleParticipantCount,
            uncheckedAlarmCount
        );
    }
}

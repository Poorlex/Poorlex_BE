package com.poorlex.refactoring.battle.battle.service.dto.response;

import com.poorlex.refactoring.battle.battle.domain.Battle;
import java.time.LocalDate;
import java.util.List;

public class BattleSpecificResponse extends BattleAndCurrentParticipantSizeResponse {

    private final long battleDDay;
    private final List<BattleParticipantResponse> rankings;

    public BattleSpecificResponse(final Long id,
                                  final String name,
                                  final String imageUrl,
                                  final String difficulty,
                                  final Long budget,
                                  final int battleParticipantCount,
                                  final int currentParticipantSize,
                                  final long battleDDay,
                                  final List<BattleParticipantResponse> rankings) {
        super(id, name, imageUrl, difficulty, budget, battleParticipantCount, currentParticipantSize);
        this.battleDDay = battleDDay;
        this.rankings = rankings;
    }

    public BattleSpecificResponse(final Battle battle,
                                  final long battleDDay,
                                  final List<BattleParticipantResponse> rankings) {
        super(battle, rankings.size());
        this.battleDDay = battleDDay;
        this.rankings = rankings;
    }

    public static BattleSpecificResponse of(final Battle battle,
                                            final LocalDate currentDate,
                                            final List<BattleParticipantResponse> participantResponses) {
        return new BattleSpecificResponse(battle, battle.getNumberOfDaysBeforeEnd(currentDate),
            participantResponses);
    }

    public long getBattleDDay() {
        return battleDDay;
    }

    public List<BattleParticipantResponse> getRankings() {
        return rankings;
    }
}

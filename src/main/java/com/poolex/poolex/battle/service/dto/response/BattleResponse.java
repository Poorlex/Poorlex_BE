package com.poolex.poolex.battle.service.dto.response;

import com.poolex.poolex.battle.domain.Battle;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BattleResponse {

    private final String battleName;
    private final int maxParticipantSize;
    private final int currentParticipantSize;
    private final int battleBudget;
    private final long battleDDay;
    private final List<ParticipantRankingResponse> rankings;

    public BattleResponse(final Battle battle, final long battleDDay, final List<ParticipantRankingResponse> rankings) {
        this.battleName = battle.getName();
        this.maxParticipantSize = battle.getMaxParticipantSize().getValue();
        this.currentParticipantSize = rankings.size();
        this.battleBudget = battle.getBudget();
        this.battleDDay = battleDDay;
        this.rankings = rankings;
    }
}

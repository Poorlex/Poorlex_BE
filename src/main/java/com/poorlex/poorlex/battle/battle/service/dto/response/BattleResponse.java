package com.poorlex.poorlex.battle.battle.service.dto.response;

import com.poorlex.poorlex.battle.battle.domain.Battle;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BattleResponse {

    private final String battleName;
    private final String battleImageUrl;
    private final int maxParticipantSize;
    private final int currentParticipantSize;
    private final int battleBudget;
    private final String battleIntroduction;
    private final long battleDDay;
    private final BattleManagerResponse battleManager;
    private final Boolean isParticipating;

    public BattleResponse(final Battle battle, final long battleDDay, final BattleManagerResponse battleManager, final int currentParticipantSize, final Boolean isParticipating) {
        this.battleName = battle.getName();
        this.battleImageUrl = battle.getImageUrl();
        this.battleIntroduction = battle.getIntroduction();
        this.maxParticipantSize = battle.getMaxParticipantSize().getValue();
        this.currentParticipantSize = currentParticipantSize;
        this.battleBudget = battle.getBudget();
        this.battleDDay = battleDDay;
        this.battleManager = battleManager;
        this.isParticipating = isParticipating;
    }
}

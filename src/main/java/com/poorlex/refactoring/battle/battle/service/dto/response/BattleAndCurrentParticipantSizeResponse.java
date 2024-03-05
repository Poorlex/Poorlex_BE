package com.poorlex.refactoring.battle.battle.service.dto.response;

import com.poorlex.refactoring.battle.battle.domain.Battle;
import com.poorlex.refactoring.battle.battle.domain.BattleWithCurrentParticipantSize;

public class BattleAndCurrentParticipantSizeResponse extends BattleBaseResponse {

    private final int currentParticipantSize;

    public BattleAndCurrentParticipantSizeResponse(final Long id,
                                                   final String name,
                                                   final String imageUrl,
                                                   final String difficulty,
                                                   final Long budget,
                                                   final int battleParticipantCount,
                                                   final int currentParticipantSize) {
        super(id, name, imageUrl, difficulty, budget, battleParticipantCount);
        this.currentParticipantSize = currentParticipantSize;
    }

    public BattleAndCurrentParticipantSizeResponse(final Battle battle,
                                                   final int currentParticipantSize) {
        super(battle);
        this.currentParticipantSize = currentParticipantSize;
    }

    public static BattleAndCurrentParticipantSizeResponse from(final BattleWithCurrentParticipantSize battleInfo) {
        return new BattleAndCurrentParticipantSizeResponse(
            battleInfo.getBattle(),
            battleInfo.getCurrentParticipantSize()
        );
    }

    public int getCurrentParticipantSize() {
        return currentParticipantSize;
    }
}

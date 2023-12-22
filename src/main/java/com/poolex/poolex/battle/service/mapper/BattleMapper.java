package com.poolex.poolex.battle.service.mapper;

import com.poolex.poolex.battle.domain.Battle;
import com.poolex.poolex.battle.domain.BattleBudget;
import com.poolex.poolex.battle.domain.BattleDuration;
import com.poolex.poolex.battle.domain.BattleImageUrl;
import com.poolex.poolex.battle.domain.BattleIntroduction;
import com.poolex.poolex.battle.domain.BattleName;
import com.poolex.poolex.battle.domain.BattleParticipantSize;
import com.poolex.poolex.battle.domain.BattleStatus;
import com.poolex.poolex.battle.service.dto.request.BattleCreateRequest;

public class BattleMapper {

    private BattleMapper() {

    }

    public static Battle createRequestToBattle(final BattleCreateRequest request) {
        return Battle.withoutBattleId(
            new BattleName(request.getName()),
            new BattleIntroduction(request.getIntroduction()),
            new BattleImageUrl(request.getImageUrl()),
            new BattleBudget(request.getBudget()),
            new BattleParticipantSize(request.getMaxParticipantSize()),
            BattleDuration.current(),
            BattleStatus.RECRUITING
        );
    }
}

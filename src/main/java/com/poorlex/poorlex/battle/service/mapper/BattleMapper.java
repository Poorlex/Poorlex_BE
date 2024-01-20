package com.poorlex.poorlex.battle.service.mapper;

import com.poorlex.poorlex.battle.domain.Battle;
import com.poorlex.poorlex.battle.domain.BattleBudget;
import com.poorlex.poorlex.battle.domain.BattleDuration;
import com.poorlex.poorlex.battle.domain.BattleImageUrl;
import com.poorlex.poorlex.battle.domain.BattleIntroduction;
import com.poorlex.poorlex.battle.domain.BattleName;
import com.poorlex.poorlex.battle.domain.BattleParticipantSize;
import com.poorlex.poorlex.battle.domain.BattleStatus;
import com.poorlex.poorlex.battle.service.dto.request.BattleCreateRequest;

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

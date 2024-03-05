package com.poorlex.refactoring.battle.battle.service.mapper;

import com.poorlex.refactoring.battle.battle.domain.Battle;
import com.poorlex.refactoring.battle.battle.domain.BattleDuration;
import com.poorlex.refactoring.battle.battle.domain.BattleFactory;
import com.poorlex.refactoring.battle.battle.domain.BattleStatus;
import com.poorlex.refactoring.battle.battle.service.dto.request.BattleCreateRequest;
import java.time.LocalDateTime;

public class BattleMapper {

    private BattleMapper() {

    }

    public static Battle createRequestToBattle(final BattleCreateRequest request) {
        final BattleDuration durationByNow = BattleDuration.fromDateTime(LocalDateTime.now());

        return BattleFactory.createWithoutId(
            request.name(),
            request.introduction(),
            request.imageUrl(),
            request.budget(),
            request.maxParticipantSize(),
            durationByNow.getStart(),
            durationByNow.getEnd(),
            BattleStatus.RECRUITING
        );
    }
}

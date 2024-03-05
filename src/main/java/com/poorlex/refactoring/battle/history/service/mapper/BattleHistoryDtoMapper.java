package com.poorlex.refactoring.battle.history.service.mapper;

import com.poorlex.refactoring.battle.battle.service.dto.BattleHistoryDto;
import com.poorlex.refactoring.battle.history.domain.BattleHistory;
import java.util.List;

public class BattleHistoryDtoMapper {

    private BattleHistoryDtoMapper() {

    }

    public static List<BattleHistoryDto> mapBy(final List<BattleHistory> battleHistories) {
        return battleHistories.stream()
            .map(BattleHistoryDtoMapper::mapBy)
            .toList();
    }

    public static BattleHistoryDto mapBy(final BattleHistory battleHistory) {
        return new BattleHistoryDto(
            battleHistory.getBattleName(),
            battleHistory.getBattleImageUrl(),
            battleHistory.getMemberBudgetLeft(),
            battleHistory.getMemberBattleRank(),
            battleHistory.getBattleMaxParticipantSize(),
            battleHistory.getMemberEarnedPoint(),
            battleHistory.getBattleEnd(),
            battleHistory.getBattleDifficulty()
        );
    }
}

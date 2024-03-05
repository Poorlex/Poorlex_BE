package com.poorlex.refactoring.battle.battle.service.validation;

import com.poorlex.refactoring.battle.battle.domain.Battle;
import com.poorlex.refactoring.battle.battle.domain.BattleRepository;
import com.poorlex.refactoring.battle.battle.domain.BattleStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BattleServiceValidator {

    private final int maxBattleCount;
    private final BattleRepository battleRepository;

    public BattleServiceValidator(@Value("${battle.max-battle-count}") final int maxBattleCount,
                                  final BattleRepository battleRepository) {
        this.maxBattleCount = maxBattleCount;
        this.battleRepository = battleRepository;
    }

    public void memberParticipateBattleUnderMaxCount(final Long memberId) {
        final int participatingBattleCount =
            battleRepository.countMemberBattleWithStatuses(memberId, BattleStatus.getReadyStatues());

        if (participatingBattleCount >= maxBattleCount) {
            throw new BattleException.MaxBattleSizeException(
                String.format("배틀은 최대 %d개만 참여할 수 있습니다.", maxBattleCount)
            );
        }
    }

    public Battle validBattleById(final Long battleId) {
        return battleRepository.findById(battleId)
            .orElseThrow(() -> new BattleException.BattleNotExistException("해당 Id를 가진 배틀이 존재하지 않습니다."));
    }
}

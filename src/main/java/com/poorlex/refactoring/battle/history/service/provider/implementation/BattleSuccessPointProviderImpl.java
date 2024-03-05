package com.poorlex.refactoring.battle.history.service.provider.implementation;

import com.poorlex.refactoring.battle.battle.domain.Battle;
import com.poorlex.refactoring.battle.battle.domain.BattleRepository;
import com.poorlex.refactoring.battle.history.service.provider.BattleSuccessPointProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BattleSuccessPointProviderImpl implements BattleSuccessPointProvider {

    private final BattleRepository battleRepository;

    @Override
    public int getPointBy(final Long battleId, final int rank) {
        final Battle battle = battleRepository.findById(battleId)
            .orElseThrow(IllegalArgumentException::new);

        return battle.getBattleSizeType().getScore(rank);
    }
}

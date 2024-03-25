package com.poorlex.poorlex.battlesuccess.service;

import com.poorlex.poorlex.battle.domain.Battle;
import com.poorlex.poorlex.battle.domain.BattleRepository;
import com.poorlex.poorlex.battlesuccess.domain.BattleSuccessHistory;
import com.poorlex.poorlex.battlesuccess.domain.BattleSuccessHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BattleSuccessService {

    private final BattleRepository battleRepository;
    private final BattleSuccessHistoryRepository battleSuccessHistoryRepository;

    @Transactional
    public void saveBattleSuccessHistory(final Long memberId, final Long battleId) {
        final Battle battle = battleRepository.findById(battleId)
                .orElseThrow(() -> new IllegalArgumentException("해당 Id의 배틀이 존재하지 않습니다."));
        final BattleSuccessHistory battleSuccessHistory =
                BattleSuccessHistory.withoutId(memberId, battle.getId(), battle.getDifficulty());

        battleSuccessHistoryRepository.save(battleSuccessHistory);
    }
}

package com.poolex.poolex.participate.service;

import com.poolex.poolex.battle.domain.Battle;
import com.poolex.poolex.battle.domain.BattleRepository;
import com.poolex.poolex.participate.domain.BattleParticipant;
import com.poolex.poolex.participate.domain.BattleParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BattleParticipantService {

    private final BattleRepository battleRepository;
    private final BattleParticipantRepository battleParticipantRepository;

    @Transactional
    public Long create(final Long battleId, final Long memberId) {
        validateBattle(battleId);
        final BattleParticipant battleParticipant = BattleParticipant.normalPlayer(battleId, memberId);
        final BattleParticipant savedBattleParticipant = battleParticipantRepository.save(battleParticipant);

        return savedBattleParticipant.getId();
    }

    private void validateBattle(final Long battleId) {
        final Battle battle = battleRepository.findById(battleId)
            .orElseThrow(IllegalArgumentException::new);
        final int battleParticipantSize = battleParticipantRepository.countBattleParticipantByBattleId(battleId);

        if (!battle.isRecruiting() || battle.hasLessOrEqualMaxParticipantSizeThen(battleParticipantSize)) {
            throw new IllegalArgumentException();
        }
    }
}

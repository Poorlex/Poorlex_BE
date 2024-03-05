package com.poorlex.refactoring.battle.participant.service.provider.implementation;

import com.poorlex.refactoring.battle.battle.service.provider.BattleParticipantCountProvider;
import com.poorlex.refactoring.battle.participant.domain.BattleParticipantRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true)
public class BattleParticipantCountProviderImpl implements BattleParticipantCountProvider {

    private BattleParticipantRepository battleParticipantRepository;

    public BattleParticipantCountProviderImpl(final BattleParticipantRepository battleParticipantRepository) {
        this.battleParticipantRepository = battleParticipantRepository;
    }

    @Override
    public int getByBattleId(final Long battleId) {
        return battleParticipantRepository.countBattleParticipantByBattleId(battleId);
    }
}

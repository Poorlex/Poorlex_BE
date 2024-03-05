package com.poorlex.refactoring.battle.participant.service.provider.implementation;

import com.poorlex.refactoring.battle.invitation.service.provider.BattleParticipantExistenceProvider;
import com.poorlex.refactoring.battle.participant.domain.BattleParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BattleParticipantExistenceProviderImpl implements BattleParticipantExistenceProvider {

    private final BattleParticipantRepository battleParticipantRepository;

    @Override
    public boolean isExistByBattleIdAndMemberId(final Long battleId, final Long memberId) {
        return battleParticipantRepository.existsByBattleIdAndMemberId(battleId, memberId);
    }

    @Override
    public boolean isExistByBattleParticipantId(final Long battleParticipantId) {
        return battleParticipantRepository.existsById(battleParticipantId);
    }
}

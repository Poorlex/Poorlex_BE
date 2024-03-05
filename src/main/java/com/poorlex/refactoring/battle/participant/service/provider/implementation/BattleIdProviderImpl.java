package com.poorlex.refactoring.battle.participant.service.provider.implementation;

import com.poorlex.refactoring.battle.invitation.service.provider.BattleIdProvider;
import com.poorlex.refactoring.battle.participant.domain.BattleParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BattleIdProviderImpl implements BattleIdProvider {

    private final BattleParticipantRepository battleParticipantRepository;

    @Override
    public Long byParticipantId(final Long battleParticipantId) {
        return battleParticipantRepository.findById(battleParticipantId)
            .orElseThrow(IllegalArgumentException::new)
            .getBattleId();
    }
}

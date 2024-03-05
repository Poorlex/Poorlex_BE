package com.poorlex.refactoring.battle.participant.service.provider.implementation;

import com.poorlex.refactoring.battle.invitation.service.provider.BattleParticipantIdProvider;
import com.poorlex.refactoring.battle.participant.domain.BattleParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BattleParticipantIdProviderImpl implements BattleParticipantIdProvider {

    private final BattleParticipantRepository battleParticipantRepository;

    @Override
    public Long byBattleIdAndMemberId(final Long battleId, final Long memberId) {
        return battleParticipantRepository.findByBattleIdAndMemberId(battleId, memberId)
            .orElseThrow(IllegalArgumentException::new)
            .getId();
    }
}

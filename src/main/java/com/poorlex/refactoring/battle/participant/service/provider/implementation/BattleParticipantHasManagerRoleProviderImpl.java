package com.poorlex.refactoring.battle.participant.service.provider.implementation;

import com.poorlex.refactoring.battle.notification.service.provider.BattleParticipantHasManagerRoleProvider;
import com.poorlex.refactoring.battle.participant.domain.BattleParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BattleParticipantHasManagerRoleProviderImpl implements BattleParticipantHasManagerRoleProvider {

    private final BattleParticipantRepository battleParticipantRepository;

    @Override
    public boolean byBattleIdAndMemberId(final Long battleId, final Long memberId) {
        return battleParticipantRepository.findByBattleIdAndMemberId(battleId, memberId)
            .orElseThrow(IllegalArgumentException::new)
            .isManager();
    }
}

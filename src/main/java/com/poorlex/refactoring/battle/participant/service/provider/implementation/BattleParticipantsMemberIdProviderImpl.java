package com.poorlex.refactoring.battle.participant.service.provider.implementation;

import com.poorlex.refactoring.battle.history.service.provider.BattleParticipantsMemberIdProvider;
import com.poorlex.refactoring.battle.participant.domain.BattleParticipant;
import com.poorlex.refactoring.battle.participant.domain.BattleParticipantRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BattleParticipantsMemberIdProviderImpl implements BattleParticipantsMemberIdProvider {

    private final BattleParticipantRepository battleParticipantRepository;

    @Override
    public List<Long> byBattleId(final Long battleId) {
        return battleParticipantRepository.findByBattleId(battleId).stream()
            .map(BattleParticipant::getId)
            .toList();
    }
}

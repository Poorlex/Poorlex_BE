package com.poorlex.refactoring.battle.participant.service.provider.implementation;

import com.poorlex.refactoring.battle.battle.service.dto.BattleParticipantDto;
import com.poorlex.refactoring.battle.battle.service.provider.BattleParticipantMemberIdAndRoleProvider;
import com.poorlex.refactoring.battle.participant.domain.BattleParticipantRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BattleParticipantMemberIdAndRoleProviderImpl implements BattleParticipantMemberIdAndRoleProvider {

    private final BattleParticipantRepository battleParticipantRepository;

    @Override
    public List<BattleParticipantDto> getByBattleId(final Long battleId) {
        return battleParticipantRepository.findByBattleId(battleId).stream()
            .map(participant -> new BattleParticipantDto(participant.getMemberId(), participant.getRole().name()))
            .toList();
    }
}

package com.poorlex.refactoring.battle.participant.service.provider.implementation;

import com.poorlex.refactoring.battle.invitation.service.provider.MemberIdProvider;
import com.poorlex.refactoring.battle.participant.domain.BattleParticipant;
import com.poorlex.refactoring.battle.participant.domain.BattleParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberIdProviderImpl implements MemberIdProvider {

    private final BattleParticipantRepository battleParticipantRepository;

    @Override
    public Long byParticipantId(final Long battleParticipantId) {
        final BattleParticipant participant = battleParticipantRepository.findById(battleParticipantId)
            .orElseThrow(IllegalArgumentException::new);
        return participant.getMemberId();
    }
}

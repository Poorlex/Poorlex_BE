package com.poolex.poolex.participate.service;

import com.poolex.poolex.auth.domain.MemberRepository;
import com.poolex.poolex.battle.domain.Battle;
import com.poolex.poolex.battle.domain.BattleRepository;
import com.poolex.poolex.config.event.Events;
import com.poolex.poolex.participate.domain.BattleParticipant;
import com.poolex.poolex.participate.domain.BattleParticipantRepository;
import com.poolex.poolex.participate.service.event.BattleParticipantAddedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BattleParticipantService {

    private final MemberRepository memberRepository;
    private final BattleRepository battleRepository;
    private final BattleParticipantRepository battleParticipantRepository;

    @Transactional
    public Long create(final Long battleId, final Long memberId) {
        validateBattle(battleId);
        validateMemberExist(memberId);
        final BattleParticipant battleParticipant = BattleParticipant.normalPlayer(battleId, memberId);
        final BattleParticipant savedBattleParticipant = battleParticipantRepository.save(battleParticipant);
        Events.raise(new BattleParticipantAddedEvent(battleId));

        return savedBattleParticipant.getId();
    }

    private void validateMemberExist(final Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new IllegalArgumentException();
        }
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

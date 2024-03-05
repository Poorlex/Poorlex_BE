package com.poorlex.refactoring.battle.participant.service;

import com.poorlex.refactoring.battle.participant.domain.BattleParticipant;
import com.poorlex.refactoring.battle.participant.domain.BattleParticipantRepository;
import com.poorlex.refactoring.battle.participant.service.event.BattleParticipantAddedEvent;
import com.poorlex.refactoring.battle.participant.service.validate.BattleParticipantValidator;
import com.poorlex.refactoring.config.event.Events;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BattleParticipantCommandService {

    private final BattleParticipantValidator validator;
    private final BattleParticipantRepository battleParticipantRepository;

    public Long participate(final Long battleId, final Long memberId) {
        validator.memberCanParticipateBattle(memberId, battleId);
        final BattleParticipant battleParticipant = BattleParticipant.normalPlayer(battleId, memberId);
        final BattleParticipant savedBattleParticipant = battleParticipantRepository.save(battleParticipant);

        Events.raise(new BattleParticipantAddedEvent(battleId));

        return savedBattleParticipant.getId();
    }

    public void withdraw(final Long battleId, final Long memberId) {
        validator.participantCanWithdrawBattle(battleId, memberId);
        final BattleParticipant participant =
            battleParticipantRepository.findByBattleIdAndMemberId(battleId, memberId).get();
        battleParticipantRepository.delete(participant);
        Events.raise(new BattleParticipantAddedEvent(battleId));
    }
}

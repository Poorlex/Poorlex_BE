package com.poorlex.refactoring.battle.participant.service.event.handler;

import com.poorlex.refactoring.battle.battle.service.event.BattleCreatedEvent;
import com.poorlex.refactoring.battle.participant.domain.BattleParticipant;
import com.poorlex.refactoring.battle.participant.domain.BattleParticipantRepository;
import com.poorlex.refactoring.battle.participant.service.validate.BattleParticipantValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@Transactional(propagation = Propagation.REQUIRED)
@RequiredArgsConstructor
public class BattleCreatedEventHandler {

    private final BattleParticipantRepository battleParticipantRepository;
    private final BattleParticipantValidator validator;

    @TransactionalEventListener(value = BattleCreatedEvent.class, phase = TransactionPhase.BEFORE_COMMIT)
    public void handle(final BattleCreatedEvent event) {
        final Long battleId = event.getBattleId();
        final Long managerId = event.getManagerId();
        validator.memberParticipatingBattleLessThanMaxBattleCount(managerId);

        battleParticipantRepository.save(BattleParticipant.manager(battleId, managerId));
    }
}

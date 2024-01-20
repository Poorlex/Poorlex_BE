package com.poorlex.poorlex.participate.service;

import com.poorlex.poorlex.battle.service.event.BattleCreatedEvent;
import com.poorlex.poorlex.participate.domain.BattleParticipant;
import com.poorlex.poorlex.participate.domain.BattleParticipantRepository;
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

    @TransactionalEventListener(value = BattleCreatedEvent.class, phase = TransactionPhase.BEFORE_COMMIT)
    public void handle(final BattleCreatedEvent event) {
        final BattleParticipant manager = BattleParticipant.manager(event.getBattleId(), event.getManagerId());
        battleParticipantRepository.save(manager);
    }
}

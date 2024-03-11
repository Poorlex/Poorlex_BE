package com.poorlex.poorlex.participate.service;

import com.poorlex.poorlex.battle.service.event.BattleCreatedEvent;
import com.poorlex.poorlex.config.event.Events;
import com.poorlex.poorlex.member.service.event.MemberDeletedEvent;
import com.poorlex.poorlex.participate.domain.BattleParticipant;
import com.poorlex.poorlex.participate.domain.BattleParticipantRepository;
import com.poorlex.poorlex.participate.service.event.BattlesWithdrewEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Service
@Transactional(propagation = Propagation.REQUIRED)
@RequiredArgsConstructor
public class BattleParticipantEventHandler {

    private final BattleParticipantRepository battleParticipantRepository;

    @TransactionalEventListener(value = BattleCreatedEvent.class, phase = TransactionPhase.BEFORE_COMMIT)
    public void handle(final BattleCreatedEvent event) {
        final BattleParticipant manager = BattleParticipant.manager(event.getBattleId(), event.getManagerId());
        battleParticipantRepository.save(manager);
    }

    @TransactionalEventListener(value = MemberDeletedEvent.class, phase = TransactionPhase.BEFORE_COMMIT)
    public void handle(final MemberDeletedEvent event) {
        final List<BattleParticipant> participants = battleParticipantRepository.findAllByMemberId(event.getMemberId());
        battleParticipantRepository.deleteAll(participants);
        final List<Long> withdrewBattleIds = participants.stream()
            .map(BattleParticipant::getBattleId)
            .toList();

        Events.raise(new BattlesWithdrewEvent(withdrewBattleIds));
    }
}

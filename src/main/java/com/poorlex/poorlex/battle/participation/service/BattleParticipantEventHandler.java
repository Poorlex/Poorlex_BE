package com.poorlex.poorlex.battle.participation.service;

import com.poorlex.poorlex.battle.battle.service.event.BattleCreatedEvent;
import com.poorlex.poorlex.battle.participation.domain.BattleParticipant;
import com.poorlex.poorlex.battle.participation.domain.BattleParticipantRepository;
import com.poorlex.poorlex.battle.participation.service.event.BattleParticipantAddedEvent;
import com.poorlex.poorlex.config.event.Events;
import com.poorlex.poorlex.user.member.service.event.MemberDeletedEvent;
import com.poorlex.poorlex.battle.participation.service.event.BattlesWithdrewEvent;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@Transactional(propagation = Propagation.REQUIRED)
@RequiredArgsConstructor
public class BattleParticipantEventHandler {

    private final BattleParticipantRepository battleParticipantRepository;

    @TransactionalEventListener(value = BattleCreatedEvent.class, phase = TransactionPhase.BEFORE_COMMIT)
    public void handle(final BattleCreatedEvent event) {
        final Long battleId = event.getBattleId();
        final BattleParticipant manager = BattleParticipant.manager(battleId, event.getManagerId());
        battleParticipantRepository.save(manager);

        Events.raise(new BattleParticipantAddedEvent(battleId, event.getManagerId()));
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

package com.poolex.poolex.participate.service;

import com.poolex.poolex.battle.service.event.BattleCreatedEvent;
import com.poolex.poolex.participate.domain.BattleParticipant;
import com.poolex.poolex.participate.domain.BattleParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BattleCreatedEventHandler {

    private final BattleParticipantRepository battleParticipantRepository;

    @EventListener(BattleCreatedEvent.class)
    public void handle(final BattleCreatedEvent event) {
        final BattleParticipant manager = BattleParticipant.manager(event.getBattleId(), event.getManagerId());
        battleParticipantRepository.save(manager);
    }
}

package com.poorlex.refactoring.battle.battle.service.event.handler;

import com.poorlex.refactoring.battle.battle.domain.Battle;
import com.poorlex.refactoring.battle.battle.service.provider.BattleParticipantCountProvider;
import com.poorlex.refactoring.battle.battle.service.validation.BattleServiceValidator;
import com.poorlex.refactoring.battle.participant.service.event.BattleParticipantAddedEvent;
import com.poorlex.refactoring.battle.participant.service.event.BattleParticipantWithdrawedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@Transactional(propagation = Propagation.REQUIRED)
@RequiredArgsConstructor
public class BattleEventHandler {

    private final BattleServiceValidator battleServiceValidator;
    private final BattleParticipantCountProvider battleParticipantCountProvider;

    @TransactionalEventListener(classes = {BattleParticipantAddedEvent.class}, phase = TransactionPhase.BEFORE_COMMIT)
    public void handle(final BattleParticipantAddedEvent event) {
        final Long battleId = event.getBattleId();
        final Battle battle = battleServiceValidator.validBattleById(battleId);
        final int currentParticipantsSize = battleParticipantCountProvider.getByBattleId(battleId);
        final int participantSizeAfterOneMoreParticipate = currentParticipantsSize + 1;

        if (!battle.isNumberOfParticipantAcceptable(participantSizeAfterOneMoreParticipate)) {
            battle.finishRecruit();
        }
    }

    @TransactionalEventListener(
        classes = {BattleParticipantWithdrawedEvent.class},
        phase = TransactionPhase.BEFORE_COMMIT
    )
    public void handle(final BattleParticipantWithdrawedEvent event) {
        final Long battleId = event.getBattleId();
        final Battle battle = battleServiceValidator.validBattleById(battleId);
        final int currentParticipantsSize = battleParticipantCountProvider.getByBattleId(battleId);
        final int participantSizeAfterOneMoreParticipate = currentParticipantsSize + 1;

        if (battle.isNumberOfParticipantAcceptable(participantSizeAfterOneMoreParticipate)) {
            battle.startRecruit();
        }
    }
}

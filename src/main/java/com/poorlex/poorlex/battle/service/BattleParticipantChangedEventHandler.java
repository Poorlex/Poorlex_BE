package com.poorlex.poorlex.battle.service;

import com.poorlex.poorlex.battle.domain.Battle;
import com.poorlex.poorlex.battle.domain.BattleRepository;
import com.poorlex.poorlex.exception.ApiException;
import com.poorlex.poorlex.exception.ExceptionTag;
import com.poorlex.poorlex.participate.domain.BattleParticipantRepository;
import com.poorlex.poorlex.participate.service.event.BattleParticipantAddedEvent;
import com.poorlex.poorlex.participate.service.event.BattlesWithdrewEvent;
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
public class BattleParticipantChangedEventHandler {

    private final BattleRepository battleRepository;
    private final BattleParticipantRepository battleParticipantRepository;

    @TransactionalEventListener(classes = {BattleParticipantAddedEvent.class}, phase = TransactionPhase.BEFORE_COMMIT)
    public void added(final BattleParticipantAddedEvent event) {
        final Long battleId = event.getBattleId();
        final int currentParticipantsSize = battleParticipantRepository.countBattleParticipantByBattleId(battleId);
        final Battle battle = battleRepository.findById(battleId)
                .orElseThrow(() -> {
                    final String errorMessage = String.format("ID에 해당하는 배틀이 존재하지 않습니다. ( ID : %d )", battleId);
                    return new ApiException(ExceptionTag.BATTLE_FIND, errorMessage);
                });

        if (battle.hasSameMaxParticipantSize(currentParticipantsSize)) {
            battle.finishRecruiting();
        }
    }

    @TransactionalEventListener(value = BattlesWithdrewEvent.class, phase = TransactionPhase.BEFORE_COMMIT)
    public void added(final BattlesWithdrewEvent event) {
        final List<Battle> withdrewBattles = battleRepository.findBattlesByIdIn(event.getBattleIds());

        withdrewBattles.stream()
                .filter(Battle::isRecruitingFinished)
                .forEach(Battle::recruit);
    }
}

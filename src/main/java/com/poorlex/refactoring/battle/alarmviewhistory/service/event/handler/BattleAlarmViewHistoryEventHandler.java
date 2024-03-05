package com.poorlex.refactoring.battle.alarmviewhistory.service.event.handler;

import com.poorlex.refactoring.battle.alarm.service.event.BattleAlarmViewedEvent;
import com.poorlex.refactoring.battle.alarmviewhistory.domain.BattleAlarmViewHistory;
import com.poorlex.refactoring.battle.alarmviewhistory.domain.BattleAlarmViewHistoryRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
@RequiredArgsConstructor
public class BattleAlarmViewHistoryEventHandler {

    private final BattleAlarmViewHistoryRepository battleAlarmViewHistoryRepository;

    @TransactionalEventListener(value = BattleAlarmViewedEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void updateOrCreateViewHistory(final BattleAlarmViewedEvent event) {
        final Long battleId = event.getBattleId();
        final Long memberId = event.getMemberId();
        final LocalDateTime viewTime = event.getViewTime();

        final BattleAlarmViewHistory battleAlarmViewHistory = getBattleAlarmViewHistory(battleId, memberId, viewTime);
        battleAlarmViewHistory.updateLastViewTime(event.getViewTime());
    }

    private BattleAlarmViewHistory getBattleAlarmViewHistory(final Long battleId,
                                                             final Long memberId,
                                                             final LocalDateTime viewTime) {
        return battleAlarmViewHistoryRepository.findByBattleIdAndMemberId(battleId, memberId)
            .orElseGet(() -> createBattleAlarmHistory(battleId, memberId, viewTime));
    }

    private BattleAlarmViewHistory createBattleAlarmHistory(final Long battleId,
                                                            final Long memberId,
                                                            final LocalDateTime viewTime) {
        return battleAlarmViewHistoryRepository.save(
            BattleAlarmViewHistory.withoutId(battleId, memberId, viewTime)
        );
    }
}

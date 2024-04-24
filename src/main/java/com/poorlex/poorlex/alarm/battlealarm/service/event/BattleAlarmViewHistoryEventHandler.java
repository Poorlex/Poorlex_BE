package com.poorlex.poorlex.alarm.battlealarm.service.event;

import com.poorlex.poorlex.alarm.battlealarm.domain.BattleAlarmViewHistory;
import com.poorlex.poorlex.alarm.battlealarm.domain.BattleAlarmViewHistoryRepository;
import com.poorlex.poorlex.battle.participation.domain.BattleParticipantRepository;
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

    private final BattleParticipantRepository battleParticipantRepository;
    private final BattleAlarmViewHistoryRepository battleAlarmViewHistoryRepository;

    @TransactionalEventListener(value = BattleAlarmViewedEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void updateOrCreateViewHistory(final BattleAlarmViewedEvent event) {
        final Long battleId = event.getBattleId();
        final Long memberId = event.getMemberId();
        final LocalDateTime viewTime = event.getViewTime();

        validateParticipantExist(battleId, memberId);
        final BattleAlarmViewHistory battleAlarmViewHistory = getBattleAlarmViewHistory(battleId, memberId, viewTime);
        battleAlarmViewHistory.updateLastViewTime(event.getViewTime());
    }

    private void validateParticipantExist(final Long battleId, final Long memberId) {
        final boolean exist = battleParticipantRepository.existsByBattleIdAndMemberId(battleId, memberId);
        if (!exist) {
            throw new IllegalArgumentException("해당 배틀 참가자가 존재하지 않습니다.");
        }
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

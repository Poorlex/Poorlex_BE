package com.poolex.poolex.alarm.service;

import com.poolex.poolex.alarm.domain.Alarm;
import com.poolex.poolex.alarm.domain.AlarmRepository;
import com.poolex.poolex.alarm.domain.AlarmType;
import com.poolex.poolex.battle.domain.Battle;
import com.poolex.poolex.battle.domain.BattleRepository;
import com.poolex.poolex.battle.domain.BattleStatus;
import com.poolex.poolex.battle.domain.BattleWithMemberExpenditure;
import com.poolex.poolex.battlenotification.service.event.BattleNotificationChangedEvent;
import com.poolex.poolex.expenditure.service.event.ExpenditureCreatedEvent;
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
public class AlarmEventHandler {

    private final AlarmRepository alarmRepository;
    private final BattleRepository battleRepository;

    @TransactionalEventListener(value = BattleNotificationChangedEvent.class, phase = TransactionPhase.BEFORE_COMMIT)
    public void notificationChangedAlarm(final BattleNotificationChangedEvent event) {
        final Alarm alarm = Alarm.withoutId(
            event.getBattlId(),
            event.getMemberId(),
            AlarmType.BATTLE_NOTIFICATION_CHANGED
        );

        alarmRepository.save(alarm);
    }

    @TransactionalEventListener(value = ExpenditureCreatedEvent.class, phase = TransactionPhase.BEFORE_COMMIT)
    public void expenditureCreateEvent(final ExpenditureCreatedEvent event) {
        final Long memberId = event.getMemberId();

        final List<BattleWithMemberExpenditure> battleInfos =
            battleRepository.findMemberBattlesByMemberIdAndStatusWithExpenditure(memberId, BattleStatus.PROGRESS);

        for (final BattleWithMemberExpenditure battleInfo : battleInfos) {
            final Battle battle = battleInfo.getBattle();
            final Integer expenditure = battleInfo.getExpenditure();

            if (!battle.hasSameStatus(BattleStatus.PROGRESS)) {
                continue;
            }

            createExpenditureCreatedAlarm(battle, memberId);
            createOverBudgetAlarmIfNeed(battle, expenditure, memberId);
        }
    }

    private void createExpenditureCreatedAlarm(final Battle battle, final Long memberId) {
        final Alarm alarm = Alarm.withoutId(battle.getId(), memberId, AlarmType.EXPENDITURE_CREATED);
        alarmRepository.save(alarm);
    }

    private void createOverBudgetAlarmIfNeed(final Battle battle, final Integer expenditure, final Long memberId) {
        if (battle.getBudgetLeft(expenditure) >= 0) {
            return;
        }
        final Alarm alarm = Alarm.withoutId(battle.getId(), memberId, AlarmType.OVER_BUDGET);
        alarmRepository.save(alarm);
    }
}

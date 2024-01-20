package com.poorlex.poorlex.alarm.battlealarm.service;

import com.poorlex.poorlex.alarm.battlealarm.domain.BattleAlarm;
import com.poorlex.poorlex.alarm.battlealarm.domain.BattleAlarmRepository;
import com.poorlex.poorlex.alarm.battlealarm.domain.BattleAlarmType;
import com.poorlex.poorlex.battle.domain.Battle;
import com.poorlex.poorlex.battle.domain.BattleRepository;
import com.poorlex.poorlex.battle.domain.BattleStatus;
import com.poorlex.poorlex.battle.domain.BattleWithMemberExpenditure;
import com.poorlex.poorlex.battlenotification.service.event.BattleNotificationChangedEvent;
import com.poorlex.poorlex.expenditure.service.event.ExpenditureCreatedEvent;
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
public class BattleAlarmEventHandler {

    private final BattleAlarmRepository battleAlarmRepository;
    private final BattleRepository battleRepository;

    @TransactionalEventListener(value = BattleNotificationChangedEvent.class, phase = TransactionPhase.BEFORE_COMMIT)
    public void notificationChangedAlarm(final BattleNotificationChangedEvent event) {
        final BattleAlarm battleAlarm = BattleAlarm.withoutId(
            event.getBattlId(),
            event.getMemberId(),
            BattleAlarmType.BATTLE_NOTIFICATION_CHANGED
        );

        battleAlarmRepository.save(battleAlarm);
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
        final BattleAlarm battleAlarm = BattleAlarm.withoutId(battle.getId(), memberId,
            BattleAlarmType.EXPENDITURE_CREATED);
        battleAlarmRepository.save(battleAlarm);
    }

    private void createOverBudgetAlarmIfNeed(final Battle battle, final Integer expenditure, final Long memberId) {
        if (battle.getBudgetLeft(expenditure) >= 0) {
            return;
        }
        final BattleAlarm battleAlarm = BattleAlarm.withoutId(battle.getId(), memberId, BattleAlarmType.OVER_BUDGET);
        battleAlarmRepository.save(battleAlarm);
    }
}

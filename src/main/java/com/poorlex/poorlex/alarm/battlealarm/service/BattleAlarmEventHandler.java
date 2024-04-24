package com.poorlex.poorlex.alarm.battlealarm.service;

import com.poorlex.poorlex.alarm.battlealarm.domain.BattleAlarm;
import com.poorlex.poorlex.alarm.battlealarm.domain.BattleAlarmRepository;
import com.poorlex.poorlex.alarm.battlealarm.domain.BattleAlarmType;
import com.poorlex.poorlex.battle.battle.domain.Battle;
import com.poorlex.poorlex.battle.battle.domain.BattleRepository;
import com.poorlex.poorlex.battle.battle.domain.BattleStatus;
import com.poorlex.poorlex.battle.battle.domain.BattleWithMemberExpenditure;
import com.poorlex.poorlex.battle.notification.service.event.BattleNotificationChangedEvent;
import com.poorlex.poorlex.consumption.expenditure.service.event.ExpenditureCreatedEvent;
import com.poorlex.poorlex.consumption.expenditure.service.event.ZeroExpenditureCreatedEvent;
import com.poorlex.poorlex.voting.vote.service.event.VoteCreatedEvent;
import com.poorlex.poorlex.voting.votingpaper.service.event.VotingPaperCreatedEvent;
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

            createExpenditureCreatedAlarm(battle.getId(), memberId);
            createOverBudgetAlarmIfNeed(battle, expenditure, memberId);
        }
    }

    private void createExpenditureCreatedAlarm(final Long battleId, final Long memberId) {
        battleAlarmRepository.save(
                BattleAlarm.withoutId(battleId, memberId, BattleAlarmType.EXPENDITURE_CREATED)
        );
    }

    private void createOverBudgetAlarmIfNeed(final Battle battle, final Integer expenditure, final Long memberId) {
        if (battle.getBudgetLeft(expenditure) >= 0) {
            return;
        }
        final BattleAlarm battleAlarm = BattleAlarm.withoutId(battle.getId(), memberId, BattleAlarmType.OVER_BUDGET);
        battleAlarmRepository.save(battleAlarm);
    }

    @TransactionalEventListener(value = ZeroExpenditureCreatedEvent.class, phase = TransactionPhase.BEFORE_COMMIT)
    public void zeroExpenditureCreateEvent(final ZeroExpenditureCreatedEvent event) {
        final List<BattleWithMemberExpenditure> battleInfos =
                battleRepository.findMemberBattlesByMemberIdAndStatusWithExpenditure(
                        event.getMemberId(),
                        BattleStatus.PROGRESS
                );

        for (BattleWithMemberExpenditure battleInfo : battleInfos) {
            createZeroExpenditureCreatedAlarm(battleInfo.getBattle().getId(), event.getMemberId());
        }
    }

    private void createZeroExpenditureCreatedAlarm(final Long battleId, final Long memberId) {
        battleAlarmRepository.save(
                BattleAlarm.withoutId(battleId, memberId, BattleAlarmType.ZERO_EXPENDITURE)
        );
    }

    @TransactionalEventListener(value = VoteCreatedEvent.class, phase = TransactionPhase.BEFORE_COMMIT)
    public void voteCreatedEvent(final VoteCreatedEvent event) {
        battleAlarmRepository.save(
                BattleAlarm.withoutId(event.getBattleId(), event.getMemberId(), BattleAlarmType.VOTE_CREATED)
        );
    }

    @TransactionalEventListener(value = VotingPaperCreatedEvent.class, phase = TransactionPhase.BEFORE_COMMIT)
    public void votingPaperCreatedEvent(final VotingPaperCreatedEvent event) {
        battleAlarmRepository.save(
                BattleAlarm.withoutId(event.getBattleId(), event.getMemberId(), BattleAlarmType.VOTING_PAPER_CREATED)
        );
    }
}

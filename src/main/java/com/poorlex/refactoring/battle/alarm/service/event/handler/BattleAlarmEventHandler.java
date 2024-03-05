package com.poorlex.refactoring.battle.alarm.service.event.handler;

import com.poorlex.poorlex.voting.vote.service.event.VoteCreatedEvent;
import com.poorlex.poorlex.voting.votingpaper.service.event.VotingPaperCreatedEvent;
import com.poorlex.refactoring.battle.alarm.domain.BattleAlarm;
import com.poorlex.refactoring.battle.alarm.domain.BattleAlarmRepository;
import com.poorlex.refactoring.battle.alarm.domain.BattleAlarmType;
import com.poorlex.refactoring.battle.alarm.service.dto.ExpenditureDurationDto;
import com.poorlex.refactoring.battle.alarm.service.dto.MemberBattleDto;
import com.poorlex.refactoring.battle.alarm.service.provider.MemberSumExpenditureProvider;
import com.poorlex.refactoring.battle.battle.domain.Battle;
import com.poorlex.refactoring.battle.battle.domain.BattleRepository;
import com.poorlex.refactoring.battle.battle.domain.BattleStatus;
import com.poorlex.refactoring.battle.notification.service.event.BattleNotificationChangedEvent;
import com.poorlex.refactoring.expenditure.service.event.ExpenditureCreatedEvent;
import java.util.List;
import java.util.stream.IntStream;
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

    private static final int ZERO_EXPENDITURE_AMOUNT = 0;

    private final BattleRepository battleRepository;
    private final BattleAlarmRepository battleAlarmRepository;
    private final MemberSumExpenditureProvider memberSumExpenditureProvider;

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

        final List<MemberBattleDto> memberBattles = getBattleWithStatus(memberId, BattleStatus.PROGRESS);
        final List<Long> memberSumExpenditureOfBattles =
            getMemberSumExpenditureOfBattlesInSameOrder(memberId, memberBattles);

        IntStream.range(0, memberBattles.size())
            .forEach(battleOrder ->
                createExpenditureAlarm(
                    memberBattles.get(battleOrder).getBattleId(),
                    memberBattles.get(battleOrder).getBudget(),
                    memberSumExpenditureOfBattles.get(battleOrder),
                    memberId
                )
            );
    }

    public List<MemberBattleDto> getBattleWithStatus(final Long memberId, final BattleStatus battleStatus) {
        final List<Battle> battles = battleRepository.findMemberBattlesByMemberIdAndStatus(memberId, battleStatus);

        return battles.stream()
            .map(battle -> new MemberBattleDto(
                battle.getId(),
                battle.getBudget(),
                battle.getStart(),
                battle.getEnd()
            ))
            .toList();
    }

    private List<Long> getMemberSumExpenditureOfBattlesInSameOrder(final Long memberId,
                                                                   final List<MemberBattleDto> memberBattles) {
        final List<ExpenditureDurationDto> battleDurations = memberBattles.stream()
            .map(battle -> new ExpenditureDurationDto(battle.getStart(), battle.getEnd()))
            .toList();

        return memberSumExpenditureProvider.betweenBattleDurationInSameOrderWithDurations(memberId, battleDurations);
    }

    private void createExpenditureAlarm(final Long battleId,
                                        final Long battleBudget,
                                        final Long memberWeeklyTotalExpenditure,
                                        final Long memberId) {
        if (memberWeeklyTotalExpenditure == ZERO_EXPENDITURE_AMOUNT) {
            battleAlarmRepository.save(BattleAlarm.withoutId(battleId, memberId, BattleAlarmType.ZERO_EXPENDITURE));
            return;
        }
        if (battleBudget >= memberWeeklyTotalExpenditure) {
            battleAlarmRepository.save(BattleAlarm.withoutId(battleId, memberId, BattleAlarmType.EXPENDITURE_CREATED));
            return;
        }
        battleAlarmRepository.save(BattleAlarm.withoutId(battleId, memberId, BattleAlarmType.OVER_BUDGET));
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

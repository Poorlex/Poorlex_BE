package com.poorlex.refactoring.battle.alarm.service;

import com.poorlex.refactoring.battle.alarm.domain.BattleAlarm;
import com.poorlex.refactoring.battle.alarm.domain.BattleAlarmRepository;
import com.poorlex.refactoring.battle.alarm.service.dto.response.BattleAlarmResponse;
import com.poorlex.refactoring.battle.alarm.service.dto.response.UncheckedBattleAlarmCountResponse;
import com.poorlex.refactoring.battle.alarm.service.event.BattleAlarmViewedEvent;
import com.poorlex.refactoring.battle.alarm.service.provider.BattleAlarmHistoryViewTimeProvider;
import com.poorlex.refactoring.config.event.Events;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BattleAlarmQueryService {

    private final BattleAlarmRepository battleAlarmRepository;
    private final BattleAlarmHistoryViewTimeProvider battleAlarmHistoryViewTimeProvider;

    public List<BattleAlarmResponse> findBattleAlarms(final Long battleId,
                                                      final Long memberId) {
        final List<BattleAlarm> battleAlarms = battleAlarmRepository.findAllByBattleId(battleId);
        battleAlarms.sort(Comparator.comparing(BattleAlarm::getCreatedAt));
        Events.raise(new BattleAlarmViewedEvent(battleId, memberId, LocalDateTime.now()));
        return BattleAlarmResponse.mapToListBy(battleAlarms);
    }

    public UncheckedBattleAlarmCountResponse getBattleParticipantUncheckedAlarmCountResponse(final Long battleId,
                                                                                             final Long memberId) {
        final int uncheckedBattleAlarmCount =
            battleAlarmHistoryViewTimeProvider.getByBattleIdAndMemberId(battleId, memberId)
                .map(historyViewTime -> getCountWhenHistoryExist(battleId, memberId, historyViewTime))
                .orElseGet(() -> getCountWhenHistoryNotExist(battleId, memberId));

        return new UncheckedBattleAlarmCountResponse(uncheckedBattleAlarmCount);
    }

    public int getBattleParticipantUncheckedAlarmCount(final Long battleId, final Long memberId) {
        return battleAlarmHistoryViewTimeProvider.getByBattleIdAndMemberId(battleId,
                memberId)
            .map(historyViewTime -> getCountWhenHistoryExist(battleId, memberId, historyViewTime))
            .orElseGet(() -> getCountWhenHistoryNotExist(battleId, memberId));
    }

    private int getCountWhenHistoryNotExist(final Long battleId, final Long memberId) {
        return battleAlarmRepository.countBattleAlarmByBattleIdAndMemberId(battleId, memberId);
    }

    private int getCountWhenHistoryExist(final Long battleId,
                                         final Long memberId,
                                         final LocalDateTime lastViewTime) {
        return battleAlarmRepository.countBattleAlarmByBattleIdAndMemberIdAndCreatedAtAfter(
            battleId,
            memberId,
            lastViewTime
        );
    }
}

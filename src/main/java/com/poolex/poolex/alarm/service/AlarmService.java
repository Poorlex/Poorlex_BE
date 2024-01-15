package com.poolex.poolex.alarm.service;

import com.poolex.poolex.alarm.domain.Alarm;
import com.poolex.poolex.alarm.domain.AlarmRepository;
import com.poolex.poolex.alarm.domain.BattleAlarmViewHistory;
import com.poolex.poolex.alarm.domain.BattleAlarmViewHistoryRepository;
import com.poolex.poolex.alarm.service.dto.request.BattleAlarmRequest;
import com.poolex.poolex.alarm.service.dto.response.BattleAlarmResponse;
import com.poolex.poolex.alarm.service.dto.response.UncheckedBattleAlarmCountResponse;
import com.poolex.poolex.alarm.service.event.BattleAlarmViewedEvent;
import com.poolex.poolex.config.event.Events;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final BattleAlarmViewHistoryRepository battleAlarmViewHistoryRepository;

    public List<BattleAlarmResponse> findBattleAlarms(final Long battleId,
                                                      final Long memberId,
                                                      final BattleAlarmRequest request) {
        final List<Alarm> battleAlarms = alarmRepository.findAllByBattleId(battleId);
        Events.raise(new BattleAlarmViewedEvent(battleId, memberId, request.getDateTime()));
        return BattleAlarmResponse.mapToList(battleAlarms);
    }

    public UncheckedBattleAlarmCountResponse getBattleParticipantUncheckedAlarmCount(final Long battleId,
                                                                                     final Long memberId) {
        final Optional<BattleAlarmViewHistory> viewHistory =
            battleAlarmViewHistoryRepository.findByBattleIdAndMemberId(battleId, memberId);

        return viewHistory.map(history -> getResponseHistoryExist(battleId, memberId, history.getLastViewTime()))
            .orElseGet(() -> getResponseHistoryNotExist(battleId, memberId));
    }

    private UncheckedBattleAlarmCountResponse getResponseHistoryNotExist(final Long battleId, final Long memberId) {
        final int uncheckedCount = alarmRepository.countAlarmByBattleIdAndMemberId(battleId, memberId);
        return new UncheckedBattleAlarmCountResponse(uncheckedCount);
    }

    private UncheckedBattleAlarmCountResponse getResponseHistoryExist(final Long battleId,
                                                                      final Long memberId,
                                                                      final LocalDateTime lastViewTime) {
        final int uncheckedAlarmCount = alarmRepository.countAlarmByBattleIdAndMemberIdAndAndCreatedAtAfter(
            battleId,
            memberId,
            lastViewTime
        );

        return new UncheckedBattleAlarmCountResponse(uncheckedAlarmCount);
    }
}

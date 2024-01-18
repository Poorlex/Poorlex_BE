package com.poolex.poolex.alarm.battlealarm.service;

import com.poolex.poolex.alarm.battlealarm.service.dto.request.BattleAlarmRequest;
import com.poolex.poolex.alarm.battlealarm.service.event.BattleAlarmViewedEvent;
import com.poolex.poolex.alarm.battlealarm.domain.BattleAlarm;
import com.poolex.poolex.alarm.battlealarm.domain.BattleAlarmRepository;
import com.poolex.poolex.alarm.battlealarm.domain.BattleAlarmViewHistory;
import com.poolex.poolex.alarm.battlealarm.domain.BattleAlarmViewHistoryRepository;
import com.poolex.poolex.alarm.battlealarm.service.dto.response.BattleAlarmResponse;
import com.poolex.poolex.alarm.battlealarm.service.dto.response.UncheckedBattleAlarmCountResponse;
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
public class BattleAlarmService {

    private final BattleAlarmRepository battleAlarmRepository;
    private final BattleAlarmViewHistoryRepository battleAlarmViewHistoryRepository;

    public List<BattleAlarmResponse> findBattleAlarms(final Long battleId,
                                                      final Long memberId,
                                                      final BattleAlarmRequest request) {
        final List<BattleAlarm> battleBattleAlarms = battleAlarmRepository.findAllByBattleId(battleId);
        Events.raise(new BattleAlarmViewedEvent(battleId, memberId, request.getDateTime()));
        return BattleAlarmResponse.mapToList(battleBattleAlarms);
    }

    public UncheckedBattleAlarmCountResponse getBattleParticipantUncheckedAlarmCount(final Long battleId,
                                                                                     final Long memberId) {
        final Optional<BattleAlarmViewHistory> viewHistory =
            battleAlarmViewHistoryRepository.findByBattleIdAndMemberId(battleId, memberId);

        return viewHistory.map(history -> getResponseHistoryExist(battleId, memberId, history.getLastViewTime()))
            .orElseGet(() -> getResponseHistoryNotExist(battleId, memberId));
    }

    private UncheckedBattleAlarmCountResponse getResponseHistoryNotExist(final Long battleId, final Long memberId) {
        final int uncheckedCount = battleAlarmRepository.countAlarmByBattleIdAndMemberId(battleId, memberId);
        return new UncheckedBattleAlarmCountResponse(uncheckedCount);
    }

    private UncheckedBattleAlarmCountResponse getResponseHistoryExist(final Long battleId,
                                                                      final Long memberId,
                                                                      final LocalDateTime lastViewTime) {
        final int uncheckedAlarmCount = battleAlarmRepository.countAlarmByBattleIdAndMemberIdAndAndCreatedAtAfter(
            battleId,
            memberId,
            lastViewTime
        );

        return new UncheckedBattleAlarmCountResponse(uncheckedAlarmCount);
    }
}

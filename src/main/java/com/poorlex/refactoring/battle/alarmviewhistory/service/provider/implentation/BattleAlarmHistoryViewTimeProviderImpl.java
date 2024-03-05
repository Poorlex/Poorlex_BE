package com.poorlex.refactoring.battle.alarmviewhistory.service.provider.implentation;

import com.poorlex.refactoring.battle.alarm.service.provider.BattleAlarmHistoryViewTimeProvider;
import com.poorlex.refactoring.battle.alarmviewhistory.domain.BattleAlarmViewHistory;
import com.poorlex.refactoring.battle.alarmviewhistory.domain.BattleAlarmViewHistoryRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BattleAlarmHistoryViewTimeProviderImpl implements BattleAlarmHistoryViewTimeProvider {

    private final BattleAlarmViewHistoryRepository battleAlarmViewHistoryRepository;

    @Override
    public Optional<LocalDateTime> getByBattleIdAndMemberId(final Long battleId, final Long memberId) {
        return battleAlarmViewHistoryRepository.findByBattleIdAndMemberId(battleId, memberId)
            .map(BattleAlarmViewHistory::getLastViewTime);
    }
}

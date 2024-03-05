package com.poorlex.refactoring.battle.alarm.service.provider;

import java.time.LocalDateTime;
import java.util.Optional;

public interface BattleAlarmHistoryViewTimeProvider {

    Optional<LocalDateTime> getByBattleIdAndMemberId(final Long battleId, final Long memberId);
}

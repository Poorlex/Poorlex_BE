package com.poolex.poolex.alarm.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BattleAlarmViewHistoryRepository extends JpaRepository<BattleAlarmViewHistory, Long> {

    Optional<BattleAlarmViewHistory> findByBattleIdAndMemberId(final Long battleId, final Long memberId);
}

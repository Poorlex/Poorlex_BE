package com.poorlex.poorlex.alarm.battlealarm.domain;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BattleAlarmRepository extends JpaRepository<BattleAlarm, Long> {

    List<BattleAlarm> findByType(final BattleAlarmType type);

    List<BattleAlarm> findAllByBattleId(final Long battleId);

    int countAlarmByBattleIdAndMemberId(final Long battleId, final Long memberId);

    int countAlarmByBattleIdAndMemberIdAndAndCreatedAtAfter(final Long battleId,
                                                            final Long memberId,
                                                            final LocalDateTime localDateTime);
}

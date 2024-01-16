package com.poolex.poolex.alarm.domain;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    List<Alarm> findByType(final AlarmType type);

    List<Alarm> findAllByBattleId(final Long battleId);

    int countAlarmByBattleIdAndMemberId(final Long battleId, final Long memberId);

    int countAlarmByBattleIdAndMemberIdAndAndCreatedAtAfter(final Long battleId,
                                                            final Long memberId,
                                                            final LocalDateTime localDateTime);
}

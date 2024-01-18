package com.poolex.poolex.alarm.battlealarm.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BattleAlarmRepository extends JpaRepository<BattleAlarm, Long> {
    
    List<BattleAlarm> findByType(final BattleAlarmType type); //추후 삭제될 테스트용 메서드

    List<BattleAlarm> findAllByBattleId(final Long battleId);
}

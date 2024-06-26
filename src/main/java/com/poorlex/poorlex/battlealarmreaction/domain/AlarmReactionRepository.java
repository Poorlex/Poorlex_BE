package com.poorlex.poorlex.battlealarmreaction.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AlarmReactionRepository extends JpaRepository<AlarmReaction, Long> {

    @Query("select ar from AlarmReaction ar "
        + "left join BattleAlarm ba on ba.id = ar.alarmId "
        + "where ba.battleId = :battleId")
    List<AlarmReaction> findAllByBattleId(@Param("battleId") final Long battleId);
}

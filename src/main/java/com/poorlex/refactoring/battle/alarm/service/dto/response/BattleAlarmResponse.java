package com.poorlex.refactoring.battle.alarm.service.dto.response;

import com.poorlex.refactoring.battle.alarm.domain.BattleAlarm;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;

@Getter
public class BattleAlarmResponse {

    private final Long alarmId;
    private final Long memberId;
    private final String alarmType;
    private final LocalDateTime createdAt;

    public BattleAlarmResponse(final Long alarmId,
                               final Long memberId,
                               final String alarmType,
                               final LocalDateTime createdAt) {
        this.alarmId = alarmId;
        this.memberId = memberId;
        this.alarmType = alarmType;
        this.createdAt = createdAt;
    }

    public static List<BattleAlarmResponse> mapToListBy(final List<BattleAlarm> battleAlarms) {
        return battleAlarms.stream()
            .map(BattleAlarmResponse::from)
            .toList();
    }

    public static BattleAlarmResponse from(final BattleAlarm battleAlarm) {
        return new BattleAlarmResponse(
            battleAlarm.getId(),
            battleAlarm.getMemberId(),
            battleAlarm.getType().name(),
            battleAlarm.getCreatedAt()
        );
    }
}

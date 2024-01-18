package com.poolex.poolex.alarm.battlealarm.service.dto.response;

import com.poolex.poolex.alarm.battlealarm.domain.BattleAlarm;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BattleAlarmResponse {

    private final Long alarmId;
    private final Long memberId;
    private final String alarmType;
    private final LocalDateTime createdAt;

    public static List<BattleAlarmResponse> generateListBy(final List<BattleAlarm> battleAlarms) {
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

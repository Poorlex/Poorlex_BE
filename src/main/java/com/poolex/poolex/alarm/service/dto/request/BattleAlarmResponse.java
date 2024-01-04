package com.poolex.poolex.alarm.service.dto.request;

import com.poolex.poolex.alarm.domain.Alarm;
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

    public static List<BattleAlarmResponse> mapToList(final List<Alarm> alarms) {
        return alarms.stream()
            .map(BattleAlarmResponse::from)
            .toList();
    }

    public static BattleAlarmResponse from(final Alarm alarm) {
        return new BattleAlarmResponse(
            alarm.getId(),
            alarm.getMemberId(),
            alarm.getType().name(),
            alarm.getCreatedAt()
        );
    }
}

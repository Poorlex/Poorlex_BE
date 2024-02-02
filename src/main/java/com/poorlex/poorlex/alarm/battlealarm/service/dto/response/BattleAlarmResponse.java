package com.poorlex.poorlex.alarm.battlealarm.service.dto.response;

import com.poorlex.poorlex.alarm.battlealarm.domain.BattleAlarm;
import com.poorlex.poorlex.common.AbstractCreatedAtResponse;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;

@Getter
public class BattleAlarmResponse extends AbstractCreatedAtResponse {

    private final Long alarmId;
    private final Long memberId;
    private final String alarmType;

    public BattleAlarmResponse(final Long alarmId,
                               final Long memberId,
                               final String alarmType,
                               final LocalDateTime createdAt) {
        super(createdAt);
        this.alarmId = alarmId;
        this.memberId = memberId;
        this.alarmType = alarmType;
    }

    public static List<BattleAlarmResponse> mapToList(final List<BattleAlarm> battleAlarms) {
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

package com.poolex.poolex.alarm.memberalram.service.dto.response;

import com.poolex.poolex.alarm.memberalram.domain.MemberAlarm;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberAlarmResponse {

    private final Long alarmId;
    private final String friendName;
    private final String battleName;
    private final String alarmType;
    private final long minutePassed;
    private final long hourPassed;
    private final long dayPassed;

    public static MemberAlarmResponse from(final MemberAlarm memberAlarm,
                                           final String friendName,
                                           final String battleName,
                                           final LocalDateTime dateTime) {
        return new MemberAlarmResponse(
            memberAlarm.getId(),
            friendName,
            battleName,
            memberAlarm.getType().name(),
            memberAlarm.getMinutePassed(dateTime),
            memberAlarm.getHourPassed(dateTime),
            memberAlarm.getDayPassed(dateTime)
        );
    }
}

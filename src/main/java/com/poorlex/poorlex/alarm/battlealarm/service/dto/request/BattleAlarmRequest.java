package com.poorlex.poorlex.alarm.battlealarm.service.dto.request;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BattleAlarmRequest {

    private LocalDateTime dateTime;

    public LocalDateTime getDateTime() {
        return dateTime.truncatedTo(ChronoUnit.MICROS);
    }
}

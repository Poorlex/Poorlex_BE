package com.poorlex.poorlex.alarm.battlealarm.service.dto.response;

import java.time.LocalDateTime;

public abstract class AbstractBattleAlarmResponse {

    private final String alarmType;
    private final LocalDateTime createdAt;

    protected AbstractBattleAlarmResponse(final String alarmType, final LocalDateTime createdAt) {
        this.alarmType = alarmType;
        this.createdAt = createdAt;
    }

    public String getAlarmType() {
        return alarmType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

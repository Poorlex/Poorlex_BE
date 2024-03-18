package com.poorlex.poorlex.alarm.alarmallowance.domain;

import java.util.Arrays;
import java.util.Optional;

public enum AlarmAllowanceType {
    EXPENDITURE_REQUEST,
    BATTLE_STATUS,
    BATTLE_CHAT,
    FRIEND,
    BATTLE_INVITE;

    public static Optional<AlarmAllowanceType> findByName(final String alarmType) {
        return Arrays.stream(values())
                .filter(value -> value.name().equalsIgnoreCase(alarmType))
                .findAny();
    }
}

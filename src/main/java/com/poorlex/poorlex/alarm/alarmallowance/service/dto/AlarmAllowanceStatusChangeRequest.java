package com.poorlex.poorlex.alarm.alarmallowance.service.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class AlarmAllowanceStatusChangeRequest {
    private final String alarmType;
    private final boolean isAllowed;
}

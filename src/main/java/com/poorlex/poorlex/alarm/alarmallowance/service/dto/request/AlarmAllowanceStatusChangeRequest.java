package com.poorlex.poorlex.alarm.alarmallowance.service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class AlarmAllowanceStatusChangeRequest {

    @Schema(description = "알림 타입",
            allowableValues = {"EXPENDITURE_REQUEST", "BATTLE_STATUS", "BATTLE_CHAT", "FRIEND", "BATTLE_INVITE"})
    private final String alarmType;
    private final boolean allowed;
}

package com.poorlex.poorlex.alarm.alarmallowance.controller;

import com.poorlex.poorlex.alarm.alarmallowance.service.AlarmAllowanceCommandService;
import com.poorlex.poorlex.alarm.alarmallowance.service.dto.AlarmAllowanceStatusChangeRequest;
import com.poorlex.poorlex.config.auth.argumentresolver.MemberInfo;
import com.poorlex.poorlex.config.auth.argumentresolver.MemberOnly;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/alarms")
@RequiredArgsConstructor
public class AlarmAllowanceCommandController {
    private final AlarmAllowanceCommandService alarmAllowanceCommandService;

    @PatchMapping("/notification")
    public ResponseEntity<Void> changeAlarmAllowanceStatus(@MemberOnly final MemberInfo memberInfo,
                                                           @RequestBody final AlarmAllowanceStatusChangeRequest request) {
        alarmAllowanceCommandService.changeAlarmAllowanceStatus(memberInfo.getMemberId(), request);
        return ResponseEntity.ok().build();
    }
}

package com.poorlex.poorlex.alarm.alarmallowance.controller;

import com.poorlex.poorlex.alarm.alarmallowance.service.AlarmAllowanceCommandService;
import com.poorlex.poorlex.alarm.alarmallowance.service.AlarmAllowanceQueryService;
import com.poorlex.poorlex.alarm.alarmallowance.service.dto.request.AlarmAllowanceStatusChangeRequest;
import com.poorlex.poorlex.alarm.alarmallowance.service.dto.response.AlarmAllowanceResponse;
import com.poorlex.poorlex.config.auth.argumentresolver.MemberInfo;
import com.poorlex.poorlex.config.auth.argumentresolver.MemberOnly;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/alarms/allowance")
@RequiredArgsConstructor
public class AlarmAllowanceCommandController implements AlarmAllowanceCommandControllerSwaggerInterface {

    private final AlarmAllowanceCommandService alarmAllowanceCommandService;
    private final AlarmAllowanceQueryService alarmAllowanceQueryService;

    @PatchMapping
    public ResponseEntity<Void> changeAlarmAllowanceStatus(
            @MemberOnly final MemberInfo memberInfo,
            @RequestBody final AlarmAllowanceStatusChangeRequest request
    ) {
        alarmAllowanceCommandService.changeAlarmAllowanceStatus(memberInfo.getMemberId(), request);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<AlarmAllowanceResponse> findAlarmAllowance(@MemberOnly final MemberInfo memberInfo) {
        return ResponseEntity.ok()
                .body(alarmAllowanceQueryService.findAllowance(memberInfo.getMemberId()));
    }
}

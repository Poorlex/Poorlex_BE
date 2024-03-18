package com.poorlex.poorlex.alarm.alarmallowance.controller;

import com.poorlex.poorlex.alarm.alarmallowance.service.dto.request.AlarmAllowanceStatusChangeRequest;
import com.poorlex.poorlex.alarm.alarmallowance.service.dto.response.AlarmAllowanceResponse;
import com.poorlex.poorlex.config.auth.argumentresolver.MemberInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;

@Tag(name = "알림 허용 관련 API")
public interface AlarmAllowanceCommandControllerSwaggerInterface {

    @Operation(summary = "알림 허용 카테고리 수정", description = "액세스 토큰 필요")
    @PatchMapping
    @ApiResponse(responseCode = "200")
    ResponseEntity<Void> changeAlarmAllowanceStatus(
            @Parameter(hidden = true) final MemberInfo memberInfo,
            @Parameter(required = true) final AlarmAllowanceStatusChangeRequest request
    );

    @Operation(summary = "알림 허용 정보 조회", description = "액세스 토큰 필요")
    @GetMapping
    @ApiResponse(responseCode = "200")
    ResponseEntity<AlarmAllowanceResponse> findAlarmAllowance(@Parameter(hidden = true) final MemberInfo memberInfo);
}

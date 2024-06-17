package com.poorlex.poorlex.alarm.memberalram.controller;

import com.poorlex.poorlex.alarm.memberalram.service.dto.response.MemberAlarmResponse;
import com.poorlex.poorlex.security.service.MemberInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@Tag(name = "회원 알림 관련 API")
public interface MemberAlarmControllerSwaggerInterface {

    @Operation(summary = "회원 알림 전체 조회", description = "액세스 토큰 필요")
    @GetMapping
    @ApiResponse(responseCode = "200")
    ResponseEntity<List<MemberAlarmResponse>> findBattleAlarms(
            @Parameter(hidden = true) final MemberInfo memberInfo);
}

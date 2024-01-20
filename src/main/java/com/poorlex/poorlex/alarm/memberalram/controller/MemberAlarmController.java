package com.poorlex.poorlex.alarm.memberalram.controller;

import com.poorlex.poorlex.alarm.memberalram.service.MemberAlarmService;
import com.poorlex.poorlex.alarm.memberalram.service.dto.request.MemberAlarmRequest;
import com.poorlex.poorlex.alarm.memberalram.service.dto.response.MemberAlarmResponse;
import com.poorlex.poorlex.config.auth.argumentresolver.MemberInfo;
import com.poorlex.poorlex.config.auth.argumentresolver.MemberOnly;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberAlarmController {

    private final MemberAlarmService memberAlarmService;

    @GetMapping("/member/alarms")
    public ResponseEntity<List<MemberAlarmResponse>> findBattleAlarms(@MemberOnly final MemberInfo memberInfo,
                                                                      @RequestBody final MemberAlarmRequest request) {
        //페이지네이션 추가 필요
        final List<MemberAlarmResponse> memberAlarms =
            memberAlarmService.findMemberAlarms(memberInfo.getMemberId(), request);

        return ResponseEntity.ok(memberAlarms);
    }
}
package com.poolex.poolex.alarm.controller;

import com.poolex.poolex.alarm.service.AlarmService;
import com.poolex.poolex.alarm.service.dto.request.BattleAlarmRequest;
import com.poolex.poolex.alarm.service.dto.response.BattleAlarmResponse;
import com.poolex.poolex.alarm.service.dto.response.UncheckedBattleAlarmCountResponse;
import com.poolex.poolex.config.auth.argumentresolver.MemberInfo;
import com.poolex.poolex.config.auth.argumentresolver.MemberOnly;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;

    @GetMapping("/battles/{battleId}/alarms")
    public ResponseEntity<List<BattleAlarmResponse>> findBattleAlarms(@MemberOnly final MemberInfo memberInfo,
                                                                      @PathVariable(name = "battleId") final Long battleId,
                                                                      @RequestBody final BattleAlarmRequest request) {
        final List<BattleAlarmResponse> battleAlarms =
            alarmService.findBattleAlarms(battleId, memberInfo.getMemberId(), request);

        return ResponseEntity.ok(battleAlarms);
    }

    @GetMapping("/battles/{battleId}/alarms/unchecked")
    public ResponseEntity<UncheckedBattleAlarmCountResponse> getUncheckedBattleAlarmCount(
        @MemberOnly final MemberInfo memberInfo,
        @PathVariable(name = "battleId") final Long battleId
    ) {
        final UncheckedBattleAlarmCountResponse response =
            alarmService.getBattleParticipantUncheckedAlarmCount(battleId, memberInfo.getMemberId());

        return ResponseEntity.ok(response);
    }

}

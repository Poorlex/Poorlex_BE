package com.poolex.poolex.alarm.battlealarm.controller;

import com.poolex.poolex.alarm.battlealarm.service.BattleAlarmService;
import com.poolex.poolex.alarm.battlealarm.service.dto.request.BattleAlarmRequest;
import com.poolex.poolex.alarm.battlealarm.service.dto.response.BattleAlarmResponse;
import com.poolex.poolex.alarm.battlealarm.service.dto.response.UncheckedBattleAlarmCountResponse;
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
public class BattleAlarmController {

    private final BattleAlarmService battleAlarmService;

    @GetMapping("/battles/{battleId}/alarms")
    public ResponseEntity<List<BattleAlarmResponse>> findBattleAlarms(@MemberOnly final MemberInfo memberInfo,
                                                                      @PathVariable(name = "battleId") final Long battleId,
                                                                      @RequestBody final BattleAlarmRequest request) {
        final List<BattleAlarmResponse> battleAlarms =
            battleAlarmService.findBattleAlarms(battleId, memberInfo.getMemberId(), request);

        return ResponseEntity.ok(battleAlarms);
    }

    @GetMapping("/battles/{battleId}/alarms/unchecked")
    public ResponseEntity<UncheckedBattleAlarmCountResponse> getUncheckedBattleAlarmCount(
        @MemberOnly final MemberInfo memberInfo,
        @PathVariable(name = "battleId") final Long battleId
    ) {
        final UncheckedBattleAlarmCountResponse response =
            battleAlarmService.getBattleParticipantUncheckedAlarmCount(battleId, memberInfo.getMemberId());

        return ResponseEntity.ok(response);
    }

}

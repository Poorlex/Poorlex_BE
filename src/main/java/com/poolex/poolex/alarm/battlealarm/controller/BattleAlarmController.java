package com.poolex.poolex.alarm.battlealarm.controller;

import com.poolex.poolex.alarm.battlealarm.service.BattleAlarmService;
import com.poolex.poolex.alarm.battlealarm.service.dto.response.BattleAlarmResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BattleAlarmController {

    private final BattleAlarmService battleAlarmService;

    @GetMapping("/battles/{battleId}/alarms")
    public ResponseEntity<List<BattleAlarmResponse>> findBattleAlarms(
        @PathVariable(name = "battleId") final Long battleId
    ) {
        final List<BattleAlarmResponse> battleAlarms = battleAlarmService.findBattleAlarms(battleId);
        return ResponseEntity.ok(battleAlarms);
    }
}

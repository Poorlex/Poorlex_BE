package com.poolex.poolex.alarm.controller;

import com.poolex.poolex.alarm.service.AlarmService;
import com.poolex.poolex.alarm.service.dto.request.BattleAlarmResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;

    @GetMapping("/battles/{battleId}/alarms")
    public ResponseEntity<List<BattleAlarmResponse>> findBattleAlarms(
        @PathVariable(name = "battleId") final Long battleId
    ) {
        final List<BattleAlarmResponse> battleAlarms = alarmService.findBattleAlarms(battleId);
        return ResponseEntity.ok(battleAlarms);
    }
}

package com.poorlex.refactoring.battle.alarm.controller;

import com.poorlex.poorlex.config.auth.argumentresolver.MemberInfo;
import com.poorlex.poorlex.config.auth.argumentresolver.MemberOnly;
import com.poorlex.refactoring.battle.alarm.service.BattleAlarmQueryService;
import com.poorlex.refactoring.battle.alarm.service.dto.response.BattleAlarmResponse;
import com.poorlex.refactoring.battle.alarm.service.dto.response.UncheckedBattleAlarmCountResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/battles/{battleId}/alarms")
@RequiredArgsConstructor
public class BattleAlarmQueryController {

    private final BattleAlarmQueryService battleAlarmQueryService;

    @GetMapping
    public ResponseEntity<List<BattleAlarmResponse>> findBattleAlarms(@MemberOnly final MemberInfo memberInfo,
                                                                      @PathVariable(name = "battleId") final Long battleId) {
        return ResponseEntity.ok(battleAlarmQueryService.findBattleAlarms(battleId, memberInfo.getMemberId()));
    }

    @GetMapping("/unchecked")
    public ResponseEntity<UncheckedBattleAlarmCountResponse> getUncheckedBattleAlarmCount(
        @MemberOnly final MemberInfo memberInfo,
        @PathVariable(name = "battleId") final Long battleId
    ) {
        return ResponseEntity.ok(
            battleAlarmQueryService.getBattleParticipantUncheckedAlarmCountResponse(battleId, memberInfo.getMemberId())
        );
    }
}

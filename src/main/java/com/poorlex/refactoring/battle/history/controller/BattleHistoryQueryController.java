package com.poorlex.refactoring.battle.history.controller;

import com.poorlex.refactoring.battle.battle.service.dto.response.BattleSuccessCountResponse;
import com.poorlex.refactoring.battle.history.service.BattleHistoryQueryService;
import com.poorlex.poorlex.config.auth.argumentresolver.MemberInfo;
import com.poorlex.poorlex.config.auth.argumentresolver.MemberOnly;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/battle-history")
public class BattleHistoryQueryController {

    private final BattleHistoryQueryService battleHistoryQueryService;

    public BattleHistoryQueryController(final BattleHistoryQueryService battleHistoryQueryService) {
        this.battleHistoryQueryService = battleHistoryQueryService;
    }

    @GetMapping("/success-count")
    public ResponseEntity<BattleSuccessCountResponse> getSuccessBattleHistoryCount(
        @MemberOnly final MemberInfo memberInfo
    ) {
        return ResponseEntity.ok(battleHistoryQueryService.findMemberBattleSuccessCounts(memberInfo.getMemberId()));
    }
}

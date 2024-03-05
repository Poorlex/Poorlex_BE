package com.poorlex.refactoring.battle.battle.controller;

import com.poorlex.refactoring.battle.battle.service.BattleQueryService;
import com.poorlex.refactoring.battle.battle.service.dto.request.BattleFindRequest;
import com.poorlex.refactoring.battle.battle.service.dto.response.BattleResponse;
import com.poorlex.refactoring.battle.battle.service.dto.response.BattleSpecificResponse;
import com.poorlex.poorlex.config.auth.argumentresolver.MemberInfo;
import com.poorlex.poorlex.config.auth.argumentresolver.MemberOnly;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/battles")
public class BattleQueryController {

    private final BattleQueryService battleQueryService;

    public BattleQueryController(final BattleQueryService battleQueryService) {
        this.battleQueryService = battleQueryService;
    }

    @GetMapping
    public ResponseEntity<List<BattleResponse>> findRecruitingBattles() {
        return ResponseEntity.ok(battleQueryService.findBattlesToPlay());
    }

    @GetMapping("/{battleId}")
    public ResponseEntity<BattleSpecificResponse> findBattle(@PathVariable("battleId") final Long battleId,
                                                             @RequestBody final BattleFindRequest request) {
        return ResponseEntity.ok(battleQueryService.findBattle(battleId, request));
    }

    @GetMapping(params = "status")
    public ResponseEntity<List<BattleResponse>> findBattlesByStatus(
        @MemberOnly final MemberInfo memberInfo,
        @RequestParam(value = "status") final String status
    ) {
        return ResponseEntity.ok(
            battleQueryService.findBattleByStatusWithCurrentDate(memberInfo.getMemberId(), status));
    }
}

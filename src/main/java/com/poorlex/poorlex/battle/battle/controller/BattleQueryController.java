package com.poorlex.poorlex.battle.battle.controller;

import com.poorlex.poorlex.battle.battle.service.BattleService;
import com.poorlex.poorlex.battle.battle.service.dto.response.BattleResponse;
import com.poorlex.poorlex.battle.battle.service.dto.response.FindingBattleResponse;
import com.poorlex.poorlex.battle.battle.service.dto.response.MemberCompleteBattleResponse;
import com.poorlex.poorlex.battle.battle.service.dto.response.MemberProgressBattleResponse;
import com.poorlex.poorlex.config.auth.argumentresolver.MemberInfo;
import com.poorlex.poorlex.config.auth.argumentresolver.MemberOnly;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/battles")
@RequiredArgsConstructor
public class BattleQueryController implements BattleQueryControllerSwaggerInterface {

    private final BattleService battleService;

    @GetMapping(value = "/{battleId}")
    public ResponseEntity<BattleResponse> getBattleInfo(@PathVariable("battleId") final Long battleId) {
        final BattleResponse battleResponse = battleService.getBattleInfo(battleId);
        return ResponseEntity.ok(battleResponse);
    }

    @GetMapping
    public ResponseEntity<List<FindingBattleResponse>> findBattles() {
        final List<FindingBattleResponse> battlesToPlay = battleService.findBattlesToPlay();
        return ResponseEntity.ok(battlesToPlay);
    }

    @GetMapping("/progress")
    public ResponseEntity<List<MemberProgressBattleResponse>> findMemberProgressBattles(
            @MemberOnly final MemberInfo memberInfo) {
        final List<MemberProgressBattleResponse> memberProgressBattleResponses =
                battleService.findProgressMemberBattles(memberInfo.getMemberId(), LocalDate.now());

        return ResponseEntity.ok(memberProgressBattleResponses);
    }

    @GetMapping("/complete")
    public ResponseEntity<List<MemberCompleteBattleResponse>> findMemberCompleteBattles(
            @MemberOnly final MemberInfo memberInfo) {
        final List<MemberCompleteBattleResponse> memberCompleteBattleResponses =
                battleService.findCompleteMemberBattles(memberInfo.getMemberId(), LocalDate.now());

        return ResponseEntity.ok(memberCompleteBattleResponses);
    }
}

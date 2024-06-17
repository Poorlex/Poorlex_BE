package com.poorlex.poorlex.battle.battle.controller;

import com.poorlex.poorlex.battle.battle.service.BattleService;
import com.poorlex.poorlex.battle.battle.service.dto.request.BattleFindRequest;
import com.poorlex.poorlex.battle.battle.service.dto.response.BattleResponse;
import com.poorlex.poorlex.battle.battle.service.dto.response.FindingBattleResponse;
import com.poorlex.poorlex.battle.battle.service.dto.response.MemberCompleteBattleResponse;
import com.poorlex.poorlex.battle.battle.service.dto.response.MemberProgressBattleResponse;
import java.time.LocalDate;
import java.util.List;
import com.poorlex.poorlex.security.service.MemberInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/battles")
@RequiredArgsConstructor
public class BattleQueryController implements BattleQueryControllerSwaggerInterface {

    private final BattleService battleService;

    @GetMapping(value = "/{battleId}")
    public ResponseEntity<BattleResponse> getBattleInfo(
            @AuthenticationPrincipal MemberInfo memberInfo,
            @PathVariable("battleId") final Long battleId) {
        final BattleResponse battleResponse = battleService.getBattleInfo(memberInfo.getId(), battleId);
        return ResponseEntity.ok(battleResponse);
    }

    @GetMapping
    public ResponseEntity<List<FindingBattleResponse>> findBattles(BattleFindRequest request, Pageable pageable) {
        final List<FindingBattleResponse> battles = battleService.queryBattles(request, pageable);
        return ResponseEntity.ok(battles);
    }

    @GetMapping("/progress")
    public ResponseEntity<List<MemberProgressBattleResponse>> findMemberProgressBattles(
            @AuthenticationPrincipal final MemberInfo memberInfo) {
        final List<MemberProgressBattleResponse> memberProgressBattleResponses =
                battleService.findProgressMemberBattles(memberInfo.getId(), LocalDate.now());

        return ResponseEntity.ok(memberProgressBattleResponses);
    }

    @GetMapping("/complete")
    public ResponseEntity<List<MemberCompleteBattleResponse>> findMemberCompleteBattles(
            @AuthenticationPrincipal final MemberInfo memberInfo) {
        final List<MemberCompleteBattleResponse> memberCompleteBattleResponses =
                battleService.findCompleteMemberBattles(memberInfo.getId(), LocalDate.now());

        return ResponseEntity.ok(memberCompleteBattleResponses);
    }
}

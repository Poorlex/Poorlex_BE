package com.poolex.poolex.battle.controller;

import com.poolex.poolex.battle.service.BattleService;
import com.poolex.poolex.battle.service.dto.request.BattleCreateRequest;
import com.poolex.poolex.battle.service.dto.response.FindingBattleResponse;
import com.poolex.poolex.battle.service.dto.response.MemberCompleteBattleResponse;
import com.poolex.poolex.battle.service.dto.response.MemberProgressBattleResponse;
import com.poolex.poolex.config.auth.argumentresolver.MemberInfo;
import com.poolex.poolex.config.auth.argumentresolver.MemberOnly;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/battles")
@RequiredArgsConstructor
public class BattleController {

    private final BattleService battleService;

    @PostMapping
    public ResponseEntity<Long> createBattle(@MemberOnly final MemberInfo memberInfo,
                                             @RequestBody final BattleCreateRequest request) {
        final Long createdBattleId = battleService.create(memberInfo.getMemberId(), request);
        return ResponseEntity.created(URI.create("/battles/" + createdBattleId)).build();
    }

    @GetMapping
    public ResponseEntity<List<FindingBattleResponse>> findBattles() {
        final List<FindingBattleResponse> battlesToPlay = battleService.findBattlesToPlay();
        return ResponseEntity.ok(battlesToPlay);
    }

    @GetMapping(params = "status=progress")
    public ResponseEntity<List<MemberProgressBattleResponse>> findMemberProgressBattles(
        @MemberOnly final MemberInfo memberInfo) {
        final List<MemberProgressBattleResponse> memberProgressBattleResponses =
            battleService.findProgressMemberBattles(memberInfo.getMemberId(), LocalDate.now());

        return ResponseEntity.ok(memberProgressBattleResponses);
    }

    @GetMapping(params = "status=complete")
    public ResponseEntity<List<MemberCompleteBattleResponse>> findMemberCompleteBattles(
        @MemberOnly final MemberInfo memberInfo) {
        final List<MemberCompleteBattleResponse> memberCompleteBattleResponses =
            battleService.findCompleteMemberBattles(memberInfo.getMemberId(), LocalDate.now());

        return ResponseEntity.ok(memberCompleteBattleResponses);
    }
}

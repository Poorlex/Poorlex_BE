package com.poorlex.poorlex.battle.controller;

import com.poorlex.poorlex.battle.service.BattleService;
import com.poorlex.poorlex.battle.service.dto.request.BattleCreateRequest;
import com.poorlex.poorlex.battle.service.dto.request.BattleFindRequest;
import com.poorlex.poorlex.battle.service.dto.response.BattleResponse;
import com.poorlex.poorlex.battle.service.dto.response.FindingBattleResponse;
import com.poorlex.poorlex.battle.service.dto.response.MemberCompleteBattleResponse;
import com.poorlex.poorlex.battle.service.dto.response.MemberProgressBattleResponse;
import com.poorlex.poorlex.config.auth.argumentresolver.MemberInfo;
import com.poorlex.poorlex.config.auth.argumentresolver.MemberOnly;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/battles")
@RequiredArgsConstructor
public class BattleController implements BattleControllerSwaggerInterface {

    private final BattleService battleService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> createBattle(@MemberOnly final MemberInfo memberInfo,
                                             @RequestPart("image") final MultipartFile image,
                                             @RequestParam final String name,
                                             @RequestParam final String introduction,
                                             @RequestParam final int budget,
                                             @RequestParam final int maxParticipantSize) {
        final BattleCreateRequest request = new BattleCreateRequest(name,
                                                                    introduction,
                                                                    budget,
                                                                    maxParticipantSize);
        final Long createdBattleId = battleService.create(memberInfo.getMemberId(), image, request);
        return ResponseEntity.created(URI.create("/battles/" + createdBattleId)).build();
    }

    @GetMapping("/{battleId}")
    public ResponseEntity<BattleResponse> getBattleInfo(@PathVariable("battleId") final Long battleId,
                                                        @RequestBody final BattleFindRequest request) {
        final BattleResponse battleResponse = battleService.getBattleInfo(battleId, request);
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

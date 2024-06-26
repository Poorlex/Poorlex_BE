package com.poorlex.poorlex.test.controller;

import com.poorlex.poorlex.batch.scheduler.BattleBatchScheduler;
import com.poorlex.poorlex.battle.battle.service.BattleService;
import com.poorlex.poorlex.battle.battle.service.dto.request.BattleCreateRequest;
import com.poorlex.poorlex.security.service.MemberInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
@Tag(name = "[테스트용] 배틀 API")
public class TestBattleController {

    private final BattleBatchScheduler battleBatchScheduler;
    private final BattleService battleService;

    @PostMapping("/battles/schedule/start")
    @Operation(summary = "모든 배틀 시작")
    @ApiResponse(responseCode = "200")
    public ResponseEntity<Void> start() {
        battleBatchScheduler.startReadiedBattles();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/battles/schedule/end")
    @Operation(summary = "모든 배틀 종료")
    @ApiResponse(responseCode = "200")
    public ResponseEntity<Void> end() {
        battleBatchScheduler.giveBattlePointToParticipants();
        battleBatchScheduler.saveBattleSuccessHistoryAndEndBattle();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "진행중 배틀 생성")
    @PostMapping(value = "/battles/progressing", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Void> createProgressingBattle(
            @Parameter(hidden = true)
            @AuthenticationPrincipal final MemberInfo memberInfo,
            @Parameter(description = "배틀 이미지", required = true)
            @RequestPart("image") final MultipartFile image,
            @Parameter(in = ParameterIn.QUERY, required = true, description = "배틀 이름")
            @RequestParam final String name,
            @Parameter(in = ParameterIn.QUERY, required = true, description = "배틀 설명")
            @RequestParam final String introduction,
            @Parameter(in = ParameterIn.QUERY, required = true, description = "배틀 예산")
            @RequestParam final int budget,
            @Parameter(in = ParameterIn.QUERY, required = true, description = "배틀 최대 인원수")
            @RequestParam final int maxParticipantSize) {
        final BattleCreateRequest request = new BattleCreateRequest(name,
                introduction,
                budget,
                maxParticipantSize);
        final Long createdBattleId = battleService.createProgressing(memberInfo.getId(), image, request);
        return ResponseEntity.created(URI.create("/battles/" + createdBattleId)).build();
    }

    @Operation(summary = "완료된 배틀 생성")
    @PostMapping(value = "/battles/completed", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Void> createCompletedBattle(
            @Parameter(hidden = true)
            @AuthenticationPrincipal final MemberInfo memberInfo,
            @Parameter(description = "배틀 이미지", required = true)
            @RequestPart("image") final MultipartFile image,
            @Parameter(in = ParameterIn.QUERY, required = true, description = "배틀 이름")
            @RequestParam final String name,
            @Parameter(in = ParameterIn.QUERY, required = true, description = "배틀 설명")
            @RequestParam final String introduction,
            @Parameter(in = ParameterIn.QUERY, required = true, description = "배틀 예산")
            @RequestParam final int budget,
            @Parameter(in = ParameterIn.QUERY, required = true, description = "배틀 최대 인원수")
            @RequestParam final int maxParticipantSize) {
        final BattleCreateRequest request = new BattleCreateRequest(name,
                introduction,
                budget,
                maxParticipantSize);
        final Long createdBattleId = battleService.createCompleted(memberInfo.getId(), image, request);
        return ResponseEntity.created(URI.create("/battles/" + createdBattleId)).build();
    }
}

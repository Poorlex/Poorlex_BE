package com.poorlex.poorlex.battle.battle.controller;

import com.poorlex.poorlex.battle.battle.service.dto.request.BattleFindRequest;
import com.poorlex.poorlex.battle.battle.service.dto.response.BattleResponse;
import com.poorlex.poorlex.battle.battle.service.dto.response.FindingBattleResponse;
import com.poorlex.poorlex.battle.battle.service.dto.response.MemberCompleteBattleResponse;
import com.poorlex.poorlex.battle.battle.service.dto.response.MemberProgressBattleResponse;
import com.poorlex.poorlex.security.service.MemberInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;

@Tag(name = "배틀 조회 관련 API")
public interface BattleQueryControllerSwaggerInterface {

    @Operation(summary = "배틀 상세 조회")
    @GetMapping("/{battleId}")
    @ApiResponse(responseCode = "200")
    ResponseEntity<BattleResponse> getBattleInfo(
            @AuthenticationPrincipal MemberInfo memberInfo,
            @Parameter(description = "배틀 Id") final Long battleId);

    @Operation(summary = "모든 배틀 조회")
    @GetMapping
    @ApiResponse(responseCode = "200")
    ResponseEntity<List<FindingBattleResponse>> findBattles(BattleFindRequest request, Pageable pageable);

    @Operation(summary = "회원 배틀 조회 ( 진행 중 )", description = "액세스 토큰 필요")
    @GetMapping("/progress")
    @ApiResponse(responseCode = "200")
    ResponseEntity<List<MemberProgressBattleResponse>> findMemberProgressBattles(
            @Parameter(hidden = true) final MemberInfo memberInfo
    );

    @Operation(summary = "회원 배틀 조회 ( 완료 )", description = "액세스 토큰 필요")
    @GetMapping("/complete")
    @ApiResponse(responseCode = "200")
    ResponseEntity<List<MemberCompleteBattleResponse>> findMemberCompleteBattles(
            @Parameter(hidden = true) final MemberInfo memberInfo
    );
}

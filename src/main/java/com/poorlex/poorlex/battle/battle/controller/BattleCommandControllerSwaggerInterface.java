package com.poorlex.poorlex.battle.battle.controller;

import com.poorlex.poorlex.security.service.MemberInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "배틀 생성 API")
public interface BattleCommandControllerSwaggerInterface {

    @Operation(summary = "배틀 생성", description = "액세스 토큰 필요")
    @PostMapping
    @ApiResponse(responseCode = "201")
    ResponseEntity<Void> createBattle(
            @Parameter(hidden = true) final MemberInfo memberInfo,
            @Parameter(description = "배틀 이미지", required = true) final MultipartFile image,
            @Parameter(in = ParameterIn.QUERY, required = true, description = "배틀 이름") final String name,
            @Parameter(in = ParameterIn.QUERY, required = true, description = "배틀 설명") final String introduction,
            @Parameter(in = ParameterIn.QUERY, required = true, description = "배틀 예산") final int budget,
            @Parameter(in = ParameterIn.QUERY, required = true, description = "배틀 최대 인원수") final int maxParticipantSize
    );

    @Operation(summary = "배틀 삭제", description = "액세스 토큰 필요, 배틀 매니저만 삭제 가능")
    @DeleteMapping("/{battleId}")
    @ApiResponse(responseCode = "204")
    ResponseEntity<Void> deleteBattle(
            @Parameter(hidden = true) final MemberInfo memberInfo,
            @Parameter(in = ParameterIn.PATH, description = "배틀 아이디")
            @PathVariable final Long battleId
    );

    @Operation(summary = "배틀 수정", description = "액세스 토큰 필요, 배틀 매니저만 수정 가능")
    @PatchMapping("/{battleId}")
    @ApiResponse(responseCode = "204")
    ResponseEntity<?> editBattle(
            @Parameter(hidden = true) final MemberInfo memberInfo,
            @Parameter(in = ParameterIn.PATH, description = "배틀 아이디") final Long battleId,
            @Parameter(description = "배틀 이미지") final MultipartFile image,
            @Parameter(in = ParameterIn.QUERY, required = true, description = "배틀 이름") final String name,
            @Parameter(in = ParameterIn.QUERY, required = true, description = "배틀 설명") final String introduction);
}

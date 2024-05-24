package com.poorlex.poorlex.battle.participation.controller;

import com.poorlex.poorlex.config.auth.argumentresolver.MemberInfo;
import com.poorlex.poorlex.config.auth.argumentresolver.MemberOnly;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Tag(name = "배틀 참가, 탈퇴 API")
public interface BattleParticipantControllerSwaggerInterface {

    @Operation(summary = "배틀 참가", description = "액세스 토큰 필요")
    @PostMapping
    @ApiResponse(responseCode = "201")
    ResponseEntity<Void> participate(@Parameter(description = "참가할 배틀 ID") final Long battleId,
                                     @Parameter(hidden = true) @MemberOnly MemberInfo memberInfo);

    @Operation(summary = "배틀 탈퇴", description = "액세스 토큰 필요")
    @DeleteMapping
    @ApiResponse(responseCode = "204")
    ResponseEntity<Void> withdraw(@Parameter(description = "탈퇴할 배틀 ID") final Long battleId,
                                  @Parameter(hidden = true) @MemberOnly MemberInfo memberInfo);
}

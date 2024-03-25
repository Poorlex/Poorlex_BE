package com.poorlex.poorlex.user.point.api;

import com.poorlex.poorlex.config.auth.argumentresolver.MemberInfo;
import com.poorlex.poorlex.user.point.service.dto.response.MemberLevelBarResponse;
import com.poorlex.poorlex.user.point.service.dto.response.MemberPointAndLevelResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@Tag(name = "회원 포인트 및 레벨 조회 API")
public interface MemberPointQueryControllerSwaggerInterface {

    @Operation(summary = "회원 포인트, 레벨 조회", description = "액세스 토큰 필요")
    @GetMapping
    @ApiResponse(responseCode = "200")
    ResponseEntity<MemberPointAndLevelResponse> findSumPointAndLevel(
            @Parameter(hidden = true) final MemberInfo memberInfo);

    @Operation(summary = "회원 레벨바 관련 포인트 조회", description = "액세스 토큰 필요")
    @GetMapping
    @ApiResponse(responseCode = "200")
    ResponseEntity<MemberLevelBarResponse> findPointsForLevelBar(@Parameter(hidden = true) final MemberInfo memberInfo);
}

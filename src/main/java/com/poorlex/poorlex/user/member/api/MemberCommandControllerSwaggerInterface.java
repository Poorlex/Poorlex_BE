package com.poorlex.poorlex.user.member.api;

import com.poorlex.poorlex.security.service.MemberInfo;
import com.poorlex.poorlex.user.member.service.dto.request.MemberProfileUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;

@Tag(name = "회원 관리 API")
public interface MemberCommandControllerSwaggerInterface {

    @Operation(summary = "회원 프로필 변경", description = "액세스 토큰 필요")
    @PatchMapping
    @ApiResponse(responseCode = "200")
    ResponseEntity<Void> changeProfile(@Parameter(hidden = true) final MemberInfo memberInfo,
                                       @Parameter(description = "프로필 변경 요청 정보", required = true)
                                       final MemberProfileUpdateRequest request);

    @Operation(summary = "회원 탈퇴", description = "액세스 토큰 필요")
    @DeleteMapping
    @ApiResponse(responseCode = "200")
    ResponseEntity<Void> deleteMember(@Parameter(hidden = true) final MemberInfo memberInfo);
}

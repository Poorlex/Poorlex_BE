package com.poorlex.poorlex.user.point.api;

import com.poorlex.poorlex.security.service.MemberInfo;
import com.poorlex.poorlex.user.point.service.dto.request.PointCreateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;

@Tag(name = "회원 포인트 지급 API")
public interface MemberPointCommandControllerSwaggerInterface {

    @Operation(summary = "회원 포인트 지급", description = "액세스 토큰 필요")
    @PostMapping
    @ApiResponse(responseCode = "201")
    ResponseEntity<Void> createPoint(
            @Parameter(hidden = true) @AuthenticationPrincipal final MemberInfo memberInfo,
            @Parameter(description = "포인트 지급 요청", required = true) final PointCreateRequest request
    );
}

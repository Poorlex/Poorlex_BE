package com.poorlex.poorlex.user.member.api;

import com.poorlex.poorlex.security.service.MemberInfo;
import com.poorlex.poorlex.user.member.service.dto.response.MyPageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@Tag(name = "회원 정보 조회 API")
public interface MemberQueryControllerSwaggerInterface {

    @Operation(summary = "회원 마이페이지 정보 조회", description = "액세스 토큰 필요")
    @GetMapping
    @ApiResponse(responseCode = "200")
    ResponseEntity<MyPageResponse> showMyPageInfo(@Parameter(hidden = true) final MemberInfo memberInfo);

}

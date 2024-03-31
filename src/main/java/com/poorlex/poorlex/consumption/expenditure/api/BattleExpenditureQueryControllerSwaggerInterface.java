package com.poorlex.poorlex.consumption.expenditure.api;

import com.poorlex.poorlex.config.auth.argumentresolver.MemberInfo;
import com.poorlex.poorlex.consumption.expenditure.service.dto.response.BattleExpenditureResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@Tag(name = "배틀 지출 조회 API")
public interface BattleExpenditureQueryControllerSwaggerInterface {

    @Operation(summary = "회원 배틀 기간 지출 목록 조회", description = "액세스 토큰 필요")
    @GetMapping(value = "/battles/{battleId}/expenditures/member", headers = HttpHeaders.AUTHORIZATION)
    @ApiResponse(responseCode = "200")
    ResponseEntity<List<BattleExpenditureResponse>> findMemberBattleExpenditures(
            @Parameter(hidden = true) final MemberInfo memberInfo,
            @Parameter(description = "배틀 Id", required = true) final Long battleId
    );

    @Operation(summary = "배틀 요일별 참가자 지출 목록 조회", description = "액세스 토큰 필요")
    @GetMapping(value = "/battles/{battleId}/expenditures", params = "dayOfWeek")
    @ApiResponse(responseCode = "200")
    ResponseEntity<List<BattleExpenditureResponse>> findBattleExpenditures(
            @Parameter(hidden = true) final MemberInfo memberInfo,
            @Parameter(description = "배틀 Id", required = true) final Long battleId,
            @Parameter(
                    in = ParameterIn.QUERY,
                    description = "지출 조회할 요일 [ MONDAY |  ... | SUNDAY ]",
                    required = true
            ) final String dayOfWeek
    );

}

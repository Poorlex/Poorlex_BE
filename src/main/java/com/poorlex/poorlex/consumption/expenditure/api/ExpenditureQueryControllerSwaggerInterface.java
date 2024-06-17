package com.poorlex.poorlex.consumption.expenditure.api;

import com.poorlex.poorlex.consumption.expenditure.service.dto.response.ExpenditureResponse;
import com.poorlex.poorlex.consumption.expenditure.service.dto.response.MemberWeeklyTotalExpenditureResponse;
import com.poorlex.poorlex.security.service.MemberInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@Tag(name = "지출 조회 API")
public interface ExpenditureQueryControllerSwaggerInterface {

    @Operation(summary = "지출 상세 조회")
    @GetMapping("/expenditures/{expenditureId}")
    @ApiResponse(responseCode = "200")
    ResponseEntity<ExpenditureResponse> findExpenditure(
            @Parameter(description = "조회하려는 지출 ID", required = true) Long expenditureId
    );

    @Operation(summary = "회원 지출 목록 조회", description = "액세스 토큰 필요")
    @GetMapping("/expenditures")
    @ApiResponse(responseCode = "200")
    ResponseEntity<List<ExpenditureResponse>> findMemberExpenditures(
            @Parameter(hidden = true) MemberInfo memberInfo
    );

    @Operation(summary = "회원 주간 총 지출 조회 ( 요청 시간 기준 )", description = "액세스 토큰 필요")
    @GetMapping("/expenditures/weekly")
    @ApiResponse(responseCode = "200")
    ResponseEntity<MemberWeeklyTotalExpenditureResponse> findMemberWeeklyTotalExpenditures(
            @Parameter(hidden = true) final MemberInfo memberInfo
    );
}

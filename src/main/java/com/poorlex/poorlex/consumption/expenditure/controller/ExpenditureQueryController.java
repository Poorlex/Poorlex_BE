package com.poorlex.poorlex.consumption.expenditure.controller;

import com.poorlex.poorlex.consumption.expenditure.api.ExpenditureQueryControllerSwaggerInterface;
import com.poorlex.poorlex.consumption.expenditure.service.ExpenditureQueryService;
import com.poorlex.poorlex.consumption.expenditure.service.dto.response.ExpenditureResponse;
import com.poorlex.poorlex.consumption.expenditure.service.dto.response.MemberWeeklyTotalExpenditureResponse;
import java.util.List;
import com.poorlex.poorlex.security.service.MemberInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ExpenditureQueryController implements ExpenditureQueryControllerSwaggerInterface {

    private final ExpenditureQueryService expenditureQueryService;

    @GetMapping("/expenditures")
    public ResponseEntity<List<ExpenditureResponse>> findMemberExpenditures(@AuthenticationPrincipal final MemberInfo memberInfo,
                                                                            final Pageable pageable) {
        final List<ExpenditureResponse> responses =
                expenditureQueryService.findMemberExpenditures(memberInfo.getId(), pageable);

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/expenditures/{expenditureId}")
    public ResponseEntity<ExpenditureResponse> findExpenditure(
            @PathVariable(name = "expenditureId") final Long expenditureId
    ) {
        final ExpenditureResponse response = expenditureQueryService.findExpenditureById(expenditureId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/expenditures/weekly")
    public ResponseEntity<MemberWeeklyTotalExpenditureResponse> findMemberWeeklyTotalExpenditures(
            @AuthenticationPrincipal final MemberInfo memberInfo
    ) {
        return ResponseEntity.ok()
                .body(expenditureQueryService.findMemberCurrentWeeklyTotalExpenditure(memberInfo.getId()));
    }
}

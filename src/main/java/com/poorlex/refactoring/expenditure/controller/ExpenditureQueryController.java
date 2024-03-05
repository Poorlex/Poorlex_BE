package com.poorlex.refactoring.expenditure.controller;

import com.poorlex.refactoring.expenditure.service.ExpenditureQueryService;
import com.poorlex.refactoring.expenditure.service.dto.response.ExpenditureResponse;
import com.poorlex.refactoring.expenditure.service.dto.response.MemberWeeklyTotalExpenditureResponse;
import com.poorlex.poorlex.config.auth.argumentresolver.MemberInfo;
import com.poorlex.poorlex.config.auth.argumentresolver.MemberOnly;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/expenditures")
@RequiredArgsConstructor
public class ExpenditureQueryController {

    private final ExpenditureQueryService expenditureService;

    @GetMapping
    public ResponseEntity<List<ExpenditureResponse>> findMemberExpenditures(@MemberOnly final MemberInfo memberInfo) {
        return ResponseEntity.ok()
            .body(expenditureService.findMemberExpenditures(memberInfo.getMemberId()));
    }

    @GetMapping("/{expenditureId}")
    public ResponseEntity<ExpenditureResponse> findExpenditure(
        @PathVariable(name = "expenditureId") final Long expenditureId
    ) {
        return ResponseEntity.ok()
            .body(expenditureService.findExpenditure(expenditureId));
    }

    @GetMapping("/weekly")
    public ResponseEntity<MemberWeeklyTotalExpenditureResponse> findMemberCurrentWeeklyTotalExpenditure(
        @MemberOnly final MemberInfo memberInfo
    ) {
        return ResponseEntity.ok()
            .body(expenditureService.findMemberCurrentWeeklyTotalExpenditure(memberInfo.getMemberId()));
    }
}

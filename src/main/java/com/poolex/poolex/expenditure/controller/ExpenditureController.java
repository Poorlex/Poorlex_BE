package com.poolex.poolex.expenditure.controller;

import com.poolex.poolex.config.auth.argumentresolver.MemberInfo;
import com.poolex.poolex.config.auth.argumentresolver.MemberOnly;
import com.poolex.poolex.expenditure.service.ExpenditureService;
import com.poolex.poolex.expenditure.service.dto.request.ExpenditureCreateRequest;
import com.poolex.poolex.expenditure.service.dto.request.MemberWeeklyTotalExpenditureRequest;
import com.poolex.poolex.expenditure.service.dto.response.MemberWeeklyTotalExpenditureResponse;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/expenditures")
@RequiredArgsConstructor
public class ExpenditureController {

    private static final String CONTROLLER_MAPPED_URL = "/expenditures";
    private final ExpenditureService expenditureService;

    @PostMapping
    public ResponseEntity<Void> createExpenditure(@MemberOnly MemberInfo memberInfo,
                                                  @RequestBody ExpenditureCreateRequest request) {
        final Long expenditureID = expenditureService.createExpenditure(memberInfo.getMemberId(), request);
        final String locationHeader = CONTROLLER_MAPPED_URL + "/" + expenditureID;

        return ResponseEntity.created(URI.create(locationHeader)).build();
    }

    @GetMapping("/weekly")
    public ResponseEntity<MemberWeeklyTotalExpenditureResponse> findMemberWeeklyTotalExpenditures(
        @MemberOnly MemberInfo memberInfo,
        @RequestBody MemberWeeklyTotalExpenditureRequest request) {
        final MemberWeeklyTotalExpenditureResponse response = expenditureService.findMemberWeeklyTotalExpenditure(
            memberInfo.getMemberId(),
            request
        );

        return ResponseEntity.ok(response);
    }

    //지출 목록 조회 API 정리, 구현
}

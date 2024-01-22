package com.poorlex.poorlex.expenditure.controller;

import com.poorlex.poorlex.config.auth.argumentresolver.MemberInfo;
import com.poorlex.poorlex.config.auth.argumentresolver.MemberOnly;
import com.poorlex.poorlex.expenditure.service.ExpenditureService;
import com.poorlex.poorlex.expenditure.service.dto.request.ExpenditureCreateRequest;
import com.poorlex.poorlex.expenditure.service.dto.request.ExpenditureUpdateRequest;
import com.poorlex.poorlex.expenditure.service.dto.request.MemberWeeklyTotalExpenditureRequest;
import com.poorlex.poorlex.expenditure.service.dto.response.BattleExpenditureResponse;
import com.poorlex.poorlex.expenditure.service.dto.response.ExpenditureResponse;
import com.poorlex.poorlex.expenditure.service.dto.response.MemberWeeklyTotalExpenditureResponse;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ExpenditureController {

    private static final String CONTROLLER_MAPPED_URL = "/expenditures";
    private final ExpenditureService expenditureService;

    @PostMapping("/expenditures")
    public ResponseEntity<Void> createExpenditure(@MemberOnly final MemberInfo memberInfo,
                                                  @RequestBody final ExpenditureCreateRequest request) {
        final Long expenditureID = expenditureService.createExpenditure(memberInfo.getMemberId(), request);
        final String locationHeader = CONTROLLER_MAPPED_URL + "/" + expenditureID;

        return ResponseEntity.created(URI.create(locationHeader)).build();
    }

    @GetMapping("/expenditures/{expenditureId}")
    public ResponseEntity<ExpenditureResponse> findExpenditure(
        @PathVariable(name = "expenditureId") final Long expenditureId
    ) {
        final ExpenditureResponse response = expenditureService.findExpenditureById(expenditureId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/expenditures")
    public ResponseEntity<List<ExpenditureResponse>> findMemberExpenditures(@MemberOnly final MemberInfo memberInfo) {
        final List<ExpenditureResponse> responses = expenditureService.findMemberExpenditures(memberInfo.getMemberId());

        return ResponseEntity.ok(responses);
    }

    @GetMapping(value = "/battles/{battleId}/expenditures", params = "dayOfWeek")
    public ResponseEntity<List<BattleExpenditureResponse>> findBattleExpenditures(
        @MemberOnly final MemberInfo memberInfo,
        @PathVariable(name = "battleId") final Long battleId,
        @RequestParam(name = "dayOfWeek") final String dayOfWeek
    ) {
        final List<BattleExpenditureResponse> responses =
            expenditureService.findBattleExpendituresInDayOfWeek(battleId, memberInfo.getMemberId(), dayOfWeek);

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/battles/{battleId}/expenditures")
    public ResponseEntity<List<BattleExpenditureResponse>> findMemberBattleExpenditures(
        @MemberOnly final MemberInfo memberInfo,
        @PathVariable(name = "battleId") final Long battleId
    ) {
        final List<BattleExpenditureResponse> responses = expenditureService.findMemberBattleExpenditures(
            battleId,
            memberInfo.getMemberId()
        );

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/expenditures/weekly")
    public ResponseEntity<MemberWeeklyTotalExpenditureResponse> findMemberWeeklyTotalExpenditures(
        @MemberOnly final MemberInfo memberInfo,
        @RequestBody final MemberWeeklyTotalExpenditureRequest request) {
        final MemberWeeklyTotalExpenditureResponse response = expenditureService.findMemberWeeklyTotalExpenditure(
            memberInfo.getMemberId(),
            request
        );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/expenditures/{expenditureId}")
    public ResponseEntity<ExpenditureResponse> updateExpenditure(@MemberOnly final MemberInfo memberInfo,
                                                                 @PathVariable(name = "expenditureId") final Long expenditureId,
                                                                 @RequestBody final ExpenditureUpdateRequest request) {
        expenditureService.updateExpenditure(memberInfo.getMemberId(), expenditureId, request);
        
        return ResponseEntity.ok().build();
    }
}

package com.poorlex.poorlex.expenditure.controller;

import com.poorlex.poorlex.config.auth.argumentresolver.MemberInfo;
import com.poorlex.poorlex.config.auth.argumentresolver.MemberOnly;
import com.poorlex.poorlex.expenditure.api.ExpenditureControllerSwaggerInterface;
import com.poorlex.poorlex.expenditure.service.ExpenditureService;
import com.poorlex.poorlex.expenditure.service.dto.request.ExpenditureCreateRequest;
import com.poorlex.poorlex.expenditure.service.dto.request.MemberWeeklyTotalExpenditureRequest;
import com.poorlex.poorlex.expenditure.service.dto.response.BattleExpenditureResponse;
import com.poorlex.poorlex.expenditure.service.dto.response.ExpenditureResponse;
import com.poorlex.poorlex.expenditure.service.dto.response.MemberWeeklyTotalExpenditureResponse;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ExpenditureController implements ExpenditureControllerSwaggerInterface {

    private static final String CONTROLLER_MAPPED_URL = "/expenditures";
    private final ExpenditureService expenditureService;

    @PostMapping(path = "/expenditures", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> createExpenditure(@MemberOnly final MemberInfo memberInfo,
                                                  @RequestPart(name = "images") final List<MultipartFile> images,
                                                  @ModelAttribute(value = "expenditure") final ExpenditureCreateRequest request) {
        final Long expenditureID = expenditureService.createExpenditure(memberInfo.getMemberId(), images, request);
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

    @GetMapping(value = "/expenditures/weekly", params = "withDate=true")
    public ResponseEntity<MemberWeeklyTotalExpenditureResponse> findMemberWeeklyTotalExpenditures(
        @MemberOnly final MemberInfo memberInfo,
        @RequestBody final MemberWeeklyTotalExpenditureRequest request) {
        final MemberWeeklyTotalExpenditureResponse response = expenditureService.findMemberWeeklyTotalExpenditure(
            memberInfo.getMemberId(),
            request.getDateTime()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/expenditures/weekly")
    public ResponseEntity<MemberWeeklyTotalExpenditureResponse> findMemberWeeklyTotalExpenditures(
        @MemberOnly final MemberInfo memberInfo
    ) {
        return ResponseEntity.ok()
            .body(expenditureService.findMemberCurrentWeeklyTotalExpenditure(memberInfo.getMemberId()));
    }

//    @PatchMapping("/expenditures/{expenditureId}")
//    public ResponseEntity<ExpenditureResponse> updateExpenditure(@MemberOnly final MemberInfo memberInfo,
//                                                                 @RequestPart(name = "file") final List<MultipartFile> images,
//                                                                 @RequestPart(value = "expenditureCreateRequest") final ExpenditureUpdateRequest request) {
//        expenditureService.updateExpenditure(memberInfo.getMemberId(), expenditureId, request);
//
//        return ResponseEntity.ok().build();
//    }
}

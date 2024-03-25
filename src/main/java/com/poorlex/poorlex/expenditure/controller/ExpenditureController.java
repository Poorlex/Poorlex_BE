package com.poorlex.poorlex.expenditure.controller;

import com.poorlex.poorlex.config.auth.argumentresolver.MemberInfo;
import com.poorlex.poorlex.config.auth.argumentresolver.MemberOnly;
import com.poorlex.poorlex.expenditure.api.ExpenditureControllerSwaggerInterface;
import com.poorlex.poorlex.expenditure.service.ExpenditureCommandService;
import com.poorlex.poorlex.expenditure.service.ExpenditureQueryService;
import com.poorlex.poorlex.expenditure.service.dto.request.ExpenditureCreateRequest;
import com.poorlex.poorlex.expenditure.service.dto.request.ExpenditureUpdateRequest;
import com.poorlex.poorlex.expenditure.service.dto.request.MemberWeeklyTotalExpenditureRequest;
import com.poorlex.poorlex.expenditure.service.dto.response.BattleExpenditureResponse;
import com.poorlex.poorlex.expenditure.service.dto.response.ExpenditureResponse;
import com.poorlex.poorlex.expenditure.service.dto.response.MemberWeeklyTotalExpenditureResponse;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ExpenditureController implements ExpenditureControllerSwaggerInterface {

    private static final String CONTROLLER_MAPPED_URL = "/expenditures";
    private final ExpenditureCommandService expenditureCommandService;
    private final ExpenditureQueryService expenditureQueryService;

    @PostMapping(path = "/expenditures", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> createExpenditure(@MemberOnly final MemberInfo memberInfo,
                                                  @RequestPart(name = "mainImage") final MultipartFile mainImage,
                                                  @RequestPart(name = "subImage", required = false)
                                                  final MultipartFile subImage,
                                                  @RequestParam(value = "amount") final Long amount,
                                                  @RequestParam(value = "description") final String description,
                                                  @RequestParam(value = "date") final LocalDate date) {
        final ExpenditureCreateRequest request = new ExpenditureCreateRequest(amount, description, date);
        final Long expenditureID = expenditureCommandService.createExpenditure(memberInfo.getMemberId(),
                                                                               mainImage,
                                                                               subImage,
                                                                               request);
        final String locationHeader = CONTROLLER_MAPPED_URL + "/" + expenditureID;

        return ResponseEntity.created(URI.create(locationHeader)).build();
    }

    @PutMapping(path = "/expenditures/{expenditureId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateExpenditure(@MemberOnly final MemberInfo memberInfo,
                                                  @PathVariable(name = "expenditureId") final Long expenditureId,
                                                  @RequestPart(name = "mainImage", required = false)
                                                  final MultipartFile mainImage,
                                                  @RequestPart(name = "mainImageUrl", required = false)
                                                  final String mainImageUrl,
                                                  @RequestPart(name = "subImage", required = false)
                                                  final MultipartFile subImage,
                                                  @RequestPart(name = "subImageUrl", required = false)
                                                  final String subImageUrl,
                                                  @RequestParam(value = "amount") final Long amount,
                                                  @RequestParam(value = "description") final String description) {
        final ExpenditureUpdateRequest request = new ExpenditureUpdateRequest(amount, description);
        expenditureCommandService.updateExpenditure(expenditureId,
                                                    memberInfo.getMemberId(),
                                                    Optional.ofNullable(mainImage),
                                                    Optional.ofNullable(mainImageUrl),
                                                    Optional.ofNullable(subImage),
                                                    Optional.ofNullable(subImageUrl),
                                                    request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/expenditures/{expenditureId}")
    public ResponseEntity<ExpenditureResponse> findExpenditure(
            @PathVariable(name = "expenditureId") final Long expenditureId
    ) {
        final ExpenditureResponse response = expenditureQueryService.findExpenditureById(expenditureId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/expenditures")
    public ResponseEntity<List<ExpenditureResponse>> findMemberExpenditures(@MemberOnly final MemberInfo memberInfo) {
        final List<ExpenditureResponse> responses =
                expenditureQueryService.findMemberExpenditures(memberInfo.getMemberId());

        return ResponseEntity.ok(responses);
    }

    @GetMapping(value = "/battles/{battleId}/expenditures", params = "dayOfWeek")
    public ResponseEntity<List<BattleExpenditureResponse>> findBattleExpenditures(
            @MemberOnly final MemberInfo memberInfo,
            @PathVariable(name = "battleId") final Long battleId,
            @RequestParam(name = "dayOfWeek") final String dayOfWeek
    ) {
        final List<BattleExpenditureResponse> responses =
                expenditureQueryService.findBattleExpendituresInDayOfWeek(battleId,
                                                                          memberInfo.getMemberId(),
                                                                          dayOfWeek);

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/battles/{battleId}/expenditures")
    public ResponseEntity<List<BattleExpenditureResponse>> findMemberBattleExpenditures(
            @MemberOnly final MemberInfo memberInfo,
            @PathVariable(name = "battleId") final Long battleId
    ) {
        final List<BattleExpenditureResponse> responses = expenditureQueryService.findMemberBattleExpenditures(
                battleId,
                memberInfo.getMemberId()
        );

        return ResponseEntity.ok(responses);
    }

    @GetMapping(value = "/expenditures/weekly", params = "withDate=true")
    public ResponseEntity<MemberWeeklyTotalExpenditureResponse> findMemberWeeklyTotalExpenditures(
            @MemberOnly final MemberInfo memberInfo,
            @RequestBody final MemberWeeklyTotalExpenditureRequest request) {
        final MemberWeeklyTotalExpenditureResponse response = expenditureQueryService.findMemberWeeklyTotalExpenditure(
                memberInfo.getMemberId(),
                request.getDate()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/expenditures/weekly")
    public ResponseEntity<MemberWeeklyTotalExpenditureResponse> findMemberWeeklyTotalExpenditures(
            @MemberOnly final MemberInfo memberInfo
    ) {
        return ResponseEntity.ok()
                .body(expenditureQueryService.findMemberCurrentWeeklyTotalExpenditure(memberInfo.getMemberId()));
    }

    @DeleteMapping("/expenditures/{expenditureId}")
    public ResponseEntity<Void> deleteExpenditure(@MemberOnly final MemberInfo memberInfo,
                                                  @PathVariable(name = "expenditureId") final Long expenditureId) {
        expenditureCommandService.deleteExpenditure(memberInfo.getMemberId(), expenditureId);
        return ResponseEntity.ok().build();
    }
}

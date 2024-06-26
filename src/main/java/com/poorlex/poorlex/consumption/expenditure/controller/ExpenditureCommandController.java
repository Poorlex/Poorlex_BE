package com.poorlex.poorlex.consumption.expenditure.controller;

import com.poorlex.poorlex.consumption.expenditure.api.ExpenditureCommandControllerSwaggerInterface;
import com.poorlex.poorlex.consumption.expenditure.service.ExpenditureCommandService;
import com.poorlex.poorlex.consumption.expenditure.service.dto.request.ExpenditureCreateRequest;
import com.poorlex.poorlex.consumption.expenditure.service.dto.request.ExpenditureUpdateRequest;
import java.net.URI;
import java.time.LocalDate;
import java.util.Optional;
import com.poorlex.poorlex.security.service.MemberInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/expenditures")
@RequiredArgsConstructor
public class ExpenditureCommandController implements ExpenditureCommandControllerSwaggerInterface {

    private final ExpenditureCommandService expenditureCommandService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> createExpenditure(@AuthenticationPrincipal final MemberInfo memberInfo,
                                                  @RequestPart(name = "mainImage") final MultipartFile mainImage,
                                                  @RequestPart(name = "subImage", required = false)
                                                  final MultipartFile subImage,
                                                  @RequestParam(value = "amount") final Long amount,
                                                  @RequestParam(value = "description") final String description,
                                                  @RequestParam(value = "date") final LocalDate date) {
        final ExpenditureCreateRequest request = new ExpenditureCreateRequest(amount, description, date);
        final Long expenditureID = expenditureCommandService.createExpenditure(memberInfo.getId(),
                                                                               mainImage,
                                                                               subImage,
                                                                               request);
        final RequestMapping requestMapping = getClass().getAnnotation(RequestMapping.class);
        final String locationHeader = requestMapping.value()[0] + "/" + expenditureID;

        return ResponseEntity.created(URI.create(locationHeader)).build();
    }

    @PutMapping(path = "/{expenditureId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateExpenditure(@AuthenticationPrincipal final MemberInfo memberInfo,
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
                                                  @RequestParam(value = "date") final LocalDate date,
                                                  @RequestParam(value = "description") final String description) {
        final ExpenditureUpdateRequest request = new ExpenditureUpdateRequest(date, amount, description);
        expenditureCommandService.updateExpenditure(expenditureId,
                                                    memberInfo.getId(),
                                                    Optional.ofNullable(mainImage),
                                                    Optional.ofNullable(mainImageUrl),
                                                    Optional.ofNullable(subImage),
                                                    Optional.ofNullable(subImageUrl),
                                                    request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{expenditureId}")
    public ResponseEntity<Void> deleteExpenditure(@AuthenticationPrincipal final MemberInfo memberInfo,
                                                  @PathVariable(name = "expenditureId") final Long expenditureId) {
        expenditureCommandService.deleteExpenditure(memberInfo.getId(), expenditureId);
        return ResponseEntity.ok().build();
    }
}

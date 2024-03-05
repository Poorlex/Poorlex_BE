package com.poorlex.refactoring.expenditure.controller;

import com.poorlex.poorlex.config.auth.argumentresolver.MemberInfo;
import com.poorlex.poorlex.config.auth.argumentresolver.MemberOnly;
import com.poorlex.refactoring.expenditure.service.ExpenditureCommandService;
import com.poorlex.refactoring.expenditure.service.dto.request.ExpenditureCreateRequest;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/expenditures")
@RequiredArgsConstructor
public class ExpenditureCommandController {

    private static final String PATH_DELIMITER = "/";
    private static final String FORM_DATA_IMAGES_KEY = "images";
    private static final String FORM_DATA_CREATE_REQUEST_KEY = "expenditureCreateRequest";

    private final ExpenditureCommandService expenditureCommandService;

    @PostMapping(path = "/expenditures", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> createExpenditure(
        @MemberOnly final MemberInfo memberInfo,
        @RequestPart(name = FORM_DATA_IMAGES_KEY) final List<MultipartFile> images,
        @RequestPart(value = FORM_DATA_CREATE_REQUEST_KEY) final ExpenditureCreateRequest request
    ) {
        final Long expenditureID = expenditureCommandService.createExpenditure(memberInfo.getMemberId(), images,
            request);
        final String controllerMappedPath = getClass().getAnnotation(RequestMapping.class).path()[0];

        return ResponseEntity.created(URI.create(controllerMappedPath + PATH_DELIMITER + expenditureID)).build();
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

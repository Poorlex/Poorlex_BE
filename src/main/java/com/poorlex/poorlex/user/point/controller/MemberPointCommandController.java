package com.poorlex.poorlex.user.point.controller;

import com.poorlex.poorlex.config.auth.argumentresolver.MemberInfo;
import com.poorlex.poorlex.config.auth.argumentresolver.MemberOnly;
import com.poorlex.poorlex.user.point.api.MemberPointCommandControllerSwaggerInterface;
import com.poorlex.poorlex.user.point.service.MemberPointCommandService;
import com.poorlex.poorlex.user.point.service.dto.request.PointCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/point")
@RequiredArgsConstructor
public class MemberPointCommandController implements MemberPointCommandControllerSwaggerInterface {

    private final MemberPointCommandService memberPointService;

    @PostMapping
    public ResponseEntity<Void> createPoint(@MemberOnly final MemberInfo memberInfo,
                                            @RequestBody final PointCreateRequest request) {
        memberPointService.createPoint(memberInfo.getMemberId(), request.getPoint());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}

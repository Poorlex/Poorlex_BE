package com.poolex.poolex.point.controller;

import com.poolex.poolex.config.auth.argumentresolver.MemberInfo;
import com.poolex.poolex.config.auth.argumentresolver.MemberOnly;
import com.poolex.poolex.point.service.MemberPointService;
import com.poolex.poolex.point.service.dto.request.PointCreateRequest;
import com.poolex.poolex.point.service.dto.response.MemberLevelBarResponse;
import com.poolex.poolex.point.service.dto.response.MemberPointResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("points")
@RequiredArgsConstructor
public class MemberPointController {

    private final MemberPointService memberPointService;

    @PostMapping
    public ResponseEntity<Void> createPoint(@MemberOnly final MemberInfo memberInfo,
                                            @RequestBody final PointCreateRequest request) {
        memberPointService.createPoint(memberInfo.getMemberId(), request.getPoint());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<MemberPointResponse> findMemberSumPointAndLevel(@MemberOnly final MemberInfo memberInfo) {
        final MemberPointResponse response = memberPointService.findMemberTotalPoint(memberInfo.getMemberId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/level-bar")
    public ResponseEntity<MemberLevelBarResponse> findPointsForLevelBar(@MemberOnly final MemberInfo memberInfo) {
        final MemberLevelBarResponse response = memberPointService.findPointsForLevelBar(memberInfo.getMemberId());
        return ResponseEntity.ok(response);
    }
}

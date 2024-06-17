package com.poorlex.poorlex.user.point.controller;

import com.poorlex.poorlex.security.service.MemberInfo;
import com.poorlex.poorlex.user.point.api.MemberPointQueryControllerSwaggerInterface;
import com.poorlex.poorlex.user.point.service.MemberPointQueryService;
import com.poorlex.poorlex.user.point.service.dto.response.MemberLevelBarResponse;
import com.poorlex.poorlex.user.point.service.dto.response.MemberPointAndLevelResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/point")
@RequiredArgsConstructor
public class MemberPointQueryController implements MemberPointQueryControllerSwaggerInterface {

    private final MemberPointQueryService memberPointQueryService;

    @GetMapping
    public ResponseEntity<MemberPointAndLevelResponse> findSumPointAndLevel(@AuthenticationPrincipal final MemberInfo memberInfo) {
        return ResponseEntity.ok()
                .body(memberPointQueryService.findMemberSumPointAndLevel(memberInfo.getId()));
    }

    @GetMapping("/level-bar")
    public ResponseEntity<MemberLevelBarResponse> findPointsForLevelBar(@AuthenticationPrincipal final MemberInfo memberInfo) {
        return ResponseEntity.ok()
                .body(memberPointQueryService.findMemberLevelBarInfo(memberInfo.getId()));
    }
}

package com.poorlex.refactoring.user.member.controller;

import com.poorlex.poorlex.config.auth.argumentresolver.MemberInfo;
import com.poorlex.poorlex.config.auth.argumentresolver.MemberOnly;
import com.poorlex.refactoring.user.member.service.MemberQueryService;
import com.poorlex.refactoring.user.member.service.dto.response.MyPageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberQueryController {

    private final MemberQueryService memberQueryService;

    @GetMapping("/my-page")
    public ResponseEntity<MyPageResponse> showMyPageInfo(@MemberOnly final MemberInfo memberInfo) {
        return ResponseEntity.ok()
            .body(memberQueryService.getMyPageInfo(memberInfo.getMemberId()));
    }
}

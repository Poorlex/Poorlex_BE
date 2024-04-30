package com.poorlex.poorlex.user.member.controller;

import com.poorlex.poorlex.config.auth.argumentresolver.MemberInfo;
import com.poorlex.poorlex.config.auth.argumentresolver.MemberOnly;
import com.poorlex.poorlex.user.member.api.MemberQueryControllerSwaggerInterface;
import com.poorlex.poorlex.user.member.service.MemberQueryService;
import com.poorlex.poorlex.user.member.service.dto.response.MyPageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberQueryController implements MemberQueryControllerSwaggerInterface {

    private final MemberQueryService memberQueryService;

    @GetMapping("/my-page")
    public ResponseEntity<MyPageResponse> showMyPageInfo(@MemberOnly final MemberInfo memberInfo) {
        final MyPageResponse response = memberQueryService.getMyPageInfoFromCurrentDatetime(memberInfo.getMemberId());
        return ResponseEntity.ok(response);
    }
}
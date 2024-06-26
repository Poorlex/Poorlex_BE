package com.poorlex.poorlex.user.member.controller;

import com.poorlex.poorlex.security.service.MemberInfo;
import com.poorlex.poorlex.user.member.api.MemberQueryControllerSwaggerInterface;
import com.poorlex.poorlex.user.member.service.MemberQueryService;
import com.poorlex.poorlex.user.member.service.dto.response.MemberProfileResponse;
import com.poorlex.poorlex.user.member.service.dto.response.MyPageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberQueryController implements MemberQueryControllerSwaggerInterface {

    private final MemberQueryService memberQueryService;

    @GetMapping("/my-page")
    public ResponseEntity<MyPageResponse> showMyPageInfo(@AuthenticationPrincipal final MemberInfo memberInfo) {
        final MyPageResponse response = memberQueryService.getMyPageInfoFromCurrentDatetime(memberInfo.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{memberId}/profile")
    public ResponseEntity<?> showMemberProfile(@PathVariable("memberId") final Long memberId) {
        final MemberProfileResponse response = memberQueryService.getMemberProfile(memberId);
        return ResponseEntity.ok(response);
    }
}

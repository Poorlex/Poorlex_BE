package com.poorlex.poorlex.member.controller;

import com.poorlex.poorlex.config.auth.argumentresolver.MemberInfo;
import com.poorlex.poorlex.config.auth.argumentresolver.MemberOnly;
import com.poorlex.poorlex.member.service.MemberService;
import com.poorlex.poorlex.member.service.dto.request.MemberProfileUpdateRequest;
import com.poorlex.poorlex.member.service.dto.response.MyPageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PatchMapping("/member/profile")
    public ResponseEntity<Void> changeProfile(@MemberOnly final MemberInfo memberInfo,
                                              @RequestBody final MemberProfileUpdateRequest request) {
        memberService.updateProfile(memberInfo.getMemberId(), request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/member/my-page")
    public ResponseEntity<MyPageResponse> showMyPageInfo(@MemberOnly final MemberInfo memberInfo) {
        final MyPageResponse response = memberService.getMyPageInfo(memberInfo.getMemberId());
        return ResponseEntity.ok(response);
    }
}

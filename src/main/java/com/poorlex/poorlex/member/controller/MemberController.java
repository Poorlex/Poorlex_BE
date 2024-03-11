package com.poorlex.poorlex.member.controller;

import com.poorlex.poorlex.config.auth.argumentresolver.MemberInfo;
import com.poorlex.poorlex.config.auth.argumentresolver.MemberOnly;
import com.poorlex.poorlex.member.service.MemberService;
import com.poorlex.poorlex.member.service.dto.request.MemberProfileUpdateRequest;
import com.poorlex.poorlex.member.service.dto.response.MyPageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController implements MemberControllerSwaggerInterface {

    private final MemberService memberService;

    @PatchMapping("/profile")
    public ResponseEntity<Void> changeProfile(@MemberOnly final MemberInfo memberInfo,
                                              @RequestBody final MemberProfileUpdateRequest request) {
        memberService.updateProfile(memberInfo.getMemberId(), request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my-page")
    public ResponseEntity<MyPageResponse> showMyPageInfo(@MemberOnly final MemberInfo memberInfo) {
        final MyPageResponse response = memberService.getMyPageInfoFromCurrentDatetime(memberInfo.getMemberId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteMember(@MemberOnly final MemberInfo memberInfo) {
        memberService.deleteMember(memberInfo.getMemberId());
        return ResponseEntity.ok().build();
    }
}

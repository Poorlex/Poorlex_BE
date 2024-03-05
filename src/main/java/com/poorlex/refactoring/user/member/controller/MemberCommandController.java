package com.poorlex.refactoring.user.member.controller;

import com.poorlex.poorlex.config.auth.argumentresolver.MemberInfo;
import com.poorlex.poorlex.config.auth.argumentresolver.MemberOnly;
import com.poorlex.refactoring.user.member.service.MemberCommandService;
import com.poorlex.refactoring.user.member.service.dto.request.MemberProfileUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberCommandController {

    private final MemberCommandService memberCommandService;

    @PatchMapping("/profile")
    public ResponseEntity<Void> changeProfile(@MemberOnly final MemberInfo memberInfo,
                                              @RequestBody final MemberProfileUpdateRequest request) {
        memberCommandService.updateProfile(memberInfo.getMemberId(), request);
        return ResponseEntity.ok().build();
    }

}

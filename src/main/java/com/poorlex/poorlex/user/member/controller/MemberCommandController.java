package com.poorlex.poorlex.user.member.controller;

import com.poorlex.poorlex.security.service.MemberInfo;
import com.poorlex.poorlex.user.member.api.MemberCommandControllerSwaggerInterface;
import com.poorlex.poorlex.user.member.service.MemberCommandService;
import com.poorlex.poorlex.user.member.service.dto.request.MemberProfileUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberCommandController implements MemberCommandControllerSwaggerInterface {

    private final MemberCommandService memberCommandService;

    @PatchMapping("/profile")
    public ResponseEntity<Void> changeProfile(@AuthenticationPrincipal final MemberInfo memberInfo,
                                              @RequestBody final MemberProfileUpdateRequest request) {
        memberCommandService.updateProfile(memberInfo.getId(), request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteMember(@AuthenticationPrincipal final MemberInfo memberInfo) {
        memberCommandService.deleteMember(memberInfo.getId());
        return ResponseEntity.ok().build();
    }
}

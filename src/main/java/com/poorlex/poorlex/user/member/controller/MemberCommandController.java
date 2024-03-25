package com.poorlex.poorlex.user.member.controller;

import com.poorlex.poorlex.config.auth.argumentresolver.MemberInfo;
import com.poorlex.poorlex.config.auth.argumentresolver.MemberOnly;
import com.poorlex.poorlex.user.member.api.MemberCommandControllerSwaggerInterface;
import com.poorlex.poorlex.user.member.service.MemberCommandService;
import com.poorlex.poorlex.user.member.service.dto.request.MemberProfileUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Void> changeProfile(@MemberOnly final MemberInfo memberInfo,
                                              @RequestBody final MemberProfileUpdateRequest request) {
        memberCommandService.updateProfile(memberInfo.getMemberId(), request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteMember(@MemberOnly final MemberInfo memberInfo) {
        memberCommandService.deleteMember(memberInfo.getMemberId());
        return ResponseEntity.ok().build();
    }
}

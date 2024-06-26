package com.poorlex.poorlex.test.controller;

import com.poorlex.poorlex.auth.service.JwtTokenProvider;
import com.poorlex.poorlex.auth.service.dto.response.LoginTokenResponse;
import com.poorlex.poorlex.exception.BadRequestException;
import com.poorlex.poorlex.exception.ConflictException;
import com.poorlex.poorlex.exception.ExceptionTag;
import com.poorlex.poorlex.user.member.domain.Member;
import com.poorlex.poorlex.user.member.domain.MemberNickname;
import com.poorlex.poorlex.user.member.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestAuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    @GetMapping("/auth/token/{nickname}")
    public ResponseEntity<?> auth(@PathVariable("nickname") String nickname) {
        List<Member> members = memberRepository.findByNickname(new MemberNickname(nickname));
        if (members.isEmpty()) {
            throw new BadRequestException(ExceptionTag.MEMBER_FIND, "해당 닉네임을 가진 멤버를 찾을 수 없습니다.");
        }

        if (members.size() > 1) {
            throw new ConflictException(ExceptionTag.MEMBER_FIND, "해당 닉네임을 가진 멤버가 여러 명 있습니다. 닉네임을 바꿔주세요");
        }

        LoginTokenResponse response = new LoginTokenResponse(
                jwtTokenProvider.createAccessToken(members.stream().findFirst().get().getId()));

        return ResponseEntity.ok(response);
    }
}

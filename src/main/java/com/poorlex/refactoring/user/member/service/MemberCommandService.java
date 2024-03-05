package com.poorlex.refactoring.user.member.service;

import com.poorlex.refactoring.user.member.domain.Member;
import com.poorlex.refactoring.user.member.domain.MemberDescription;
import com.poorlex.refactoring.user.member.domain.MemberNickname;
import com.poorlex.refactoring.user.member.domain.MemberRepository;
import com.poorlex.refactoring.user.member.service.dto.request.MemberProfileUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberCommandService {

    private final MemberRepository memberRepository;

    public void updateProfile(final Long memberId, final MemberProfileUpdateRequest request) {
        final Member member = memberRepository.findById(memberId)
            .orElseThrow(IllegalArgumentException::new);

        member.changeNickname(new MemberNickname(request.getNickname()));
        member.changeDescription(new MemberDescription(request.getDescription()));
    }
}

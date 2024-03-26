package com.poorlex.poorlex.user.member.service;

import com.poorlex.poorlex.config.event.Events;
import com.poorlex.poorlex.exception.ApiException;
import com.poorlex.poorlex.exception.ExceptionTag;
import com.poorlex.poorlex.user.member.domain.Member;
import com.poorlex.poorlex.user.member.domain.MemberDescription;
import com.poorlex.poorlex.user.member.domain.MemberNickname;
import com.poorlex.poorlex.user.member.domain.MemberRepository;
import com.poorlex.poorlex.user.member.service.dto.request.MemberProfileUpdateRequest;
import com.poorlex.poorlex.user.member.service.event.MemberDeletedEvent;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberCommandService {

    private final MemberRepository memberRepository;

    @Transactional
    public void updateProfile(final Long memberId, final MemberProfileUpdateRequest request) {
        final Member member = getMemberById(memberId);
        final String updateNickname = request.getNickname();
        final String updateDescription = request.getDescription();

        if (Objects.nonNull(updateNickname)) {
            member.changeNickname(new MemberNickname(updateNickname));
        }
        if (Objects.nonNull(updateDescription)) {
            member.changeDescription(new MemberDescription(updateDescription));
        }
    }

    public void deleteMember(final Long memberId) {
        final Member member = getMemberById(memberId);
        memberRepository.delete(member);

        Events.raise(new MemberDeletedEvent(memberId));
    }

    private Member getMemberById(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    final String errorMessage = String.format("Id 에 해당하는 회원이 존재하지 않습니다. ( ID : %d )", memberId);
                    return new ApiException(ExceptionTag.MEMBER_FIND, errorMessage);
                });
    }
}

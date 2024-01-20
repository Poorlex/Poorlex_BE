package com.poorlex.poorlex.member.service;

import com.poorlex.poorlex.member.domain.Member;
import com.poorlex.poorlex.member.domain.MemberDescription;
import com.poorlex.poorlex.member.domain.MemberIdAndNicknameDto;
import com.poorlex.poorlex.member.domain.MemberNickname;
import com.poorlex.poorlex.member.domain.MemberRepository;
import com.poorlex.poorlex.member.service.dto.request.MemberProfileUpdateRequest;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Map<Long, String> getMembersNickname(final List<Long> memberIds) {
        return memberRepository.getMemberNicknamesByMemberIds(memberIds)
            .stream()
            .collect(Collectors.toMap(MemberIdAndNicknameDto::getMemberId, MemberIdAndNicknameDto::getNickname));
    }

    @Transactional
    public void updateProfile(final Long memberId, final MemberProfileUpdateRequest request) {
        final Member member = memberRepository.findById(memberId)
            .orElseThrow(IllegalArgumentException::new);
        final String newNickname = request.getNickname();
        final String newDescription = request.getDescription();

        if (Objects.nonNull(newNickname)) {
            member.changeNickname(new MemberNickname(request.getNickname()));
        }
        if (Objects.nonNull(newDescription)) {
            member.changeDescription(new MemberDescription(request.getDescription()));
        }
    }
}

package com.poolex.poolex.member.service;

import com.poolex.poolex.member.domain.MemberIdAndNicknameDto;
import com.poolex.poolex.member.domain.MemberRepository;
import java.util.List;
import java.util.Map;
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
}

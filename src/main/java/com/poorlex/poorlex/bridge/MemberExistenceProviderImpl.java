package com.poorlex.poorlex.bridge;

import com.poorlex.poorlex.consumption.weeklybudget.service.provider.MemberExistenceProvider;
import com.poorlex.poorlex.user.member.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberExistenceProviderImpl implements MemberExistenceProvider {

    private final MemberRepository memberRepository;

    @Override
    public boolean byMemberId(final Long memberId) {
        return memberRepository.existsById(memberId);
    }
}

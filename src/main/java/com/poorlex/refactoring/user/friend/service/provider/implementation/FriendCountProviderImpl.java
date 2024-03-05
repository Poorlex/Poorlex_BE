package com.poorlex.refactoring.user.friend.service.provider.implementation;

import com.poorlex.refactoring.user.friend.domain.FriendRepository;
import com.poorlex.refactoring.user.member.service.provider.FriendCountProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FriendCountProviderImpl implements FriendCountProvider {

    private final FriendRepository friendRepository;

    @Override
    public int byMemberId(final Long memberId) {
        return friendRepository.countFriendByMemberId(memberId);
    }
}

package com.poorlex.refactoring.user.friend.service;

import com.poorlex.refactoring.battle.invitation.service.provider.FriendExistenceProvider;
import com.poorlex.refactoring.user.friend.domain.FriendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FriendExistenceProviderImpl implements FriendExistenceProvider {

    private final FriendRepository friendRepository;

    @Override
    public boolean isExist(final Long firstMemberId, final Long secondMemberId) {
        return friendRepository.existsByFirstMemberIdAndSecondMemberId(firstMemberId, secondMemberId)
            || friendRepository.existsByFirstMemberIdAndSecondMemberId(secondMemberId, firstMemberId);
    }
}

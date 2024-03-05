package com.poorlex.refactoring.user.friend.service.provider.implementation;

import com.poorlex.refactoring.user.friend.domain.FriendRepository;
import com.poorlex.refactoring.user.member.service.dto.FriendMemberIdDto;
import com.poorlex.refactoring.user.member.service.provider.FriendIdProvider;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FriendIdProviderImpl implements FriendIdProvider {

    private final FriendRepository friendRepository;

    @Override
    public List<FriendMemberIdDto> byMemberIdLimit(final Long memberId, final int limit) {
        final List<Long> friendIds =
            friendRepository.findMembersFriendMemberIdLimit(memberId, PageRequest.of(0, limit));

        return friendIds.stream()
            .map(FriendMemberIdDto::new)
            .toList();
    }
}

package com.poorlex.poorlex.bridge;

import com.poorlex.poorlex.friend.domain.FriendRepository;
import com.poorlex.poorlex.user.member.service.provider.FriendProvider;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FriendProviderImpl implements FriendProvider {

    private final FriendRepository friendRepository;

    @Override
    public List<Long> getByMemberId(final Long memberId) {
        return friendRepository.findFriendIdsByMemberId(memberId);
    }
}

package com.poorlex.poorlex.friend.service;

import com.poorlex.poorlex.friend.domain.Friend;
import com.poorlex.poorlex.friend.domain.FriendRepository;
import com.poorlex.poorlex.member.service.event.MemberDeletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Component
@Transactional(propagation = Propagation.REQUIRED)
@RequiredArgsConstructor
public class FriendEventHandler {
    private final FriendRepository friendRepository;

    @TransactionalEventListener(value = MemberDeletedEvent.class, phase = TransactionPhase.BEFORE_COMMIT)
    public void handle(final MemberDeletedEvent event) {
        final List<Friend> friends = friendRepository.findFriendsByFirstMemberIdOrSecondMemberId(event.getMemberId());
        friendRepository.deleteAll(friends);
    }
}

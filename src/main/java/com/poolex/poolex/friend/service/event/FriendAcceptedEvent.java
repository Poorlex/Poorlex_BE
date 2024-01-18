package com.poolex.poolex.friend.service.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FriendAcceptedEvent {

    private final Long inviteMemberId;
    private final Long acceptMemberId;
}

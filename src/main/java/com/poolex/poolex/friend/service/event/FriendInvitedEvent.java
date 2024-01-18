package com.poolex.poolex.friend.service.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FriendInvitedEvent {

    private final Long hostMemberId;
    private final Long invitedMemberId;
}

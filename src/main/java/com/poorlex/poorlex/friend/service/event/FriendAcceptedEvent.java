package com.poorlex.poorlex.friend.service.event;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FriendAcceptedEvent {

    private Long inviteMemberId;
    private Long acceptMemberId;
}

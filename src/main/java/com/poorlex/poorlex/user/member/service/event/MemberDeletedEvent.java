package com.poorlex.poorlex.user.member.service.event;

public class MemberDeletedEvent {

    private final Long memberId;

    public MemberDeletedEvent(final Long memberId) {
        this.memberId = memberId;
    }

    public Long getMemberId() {
        return memberId;
    }
}

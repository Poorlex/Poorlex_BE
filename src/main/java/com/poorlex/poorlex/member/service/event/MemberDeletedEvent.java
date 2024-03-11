package com.poorlex.poorlex.member.service.event;

public class MemberDeletedEvent {
    private final Long memberId;

    public MemberDeletedEvent(final Long memberId) {
        this.memberId = memberId;
    }

    public Long getMemberId() {
        return memberId;
    }
}

package com.poorlex.poorlex.member.service.event;

public class MemberRegisteredEvent {
    private final Long memberId;

    public MemberRegisteredEvent(final Long memberId) {
        this.memberId = memberId;
    }

    public Long getMemberId() {
        return memberId;
    }
}

package com.poorlex.poorlex.consumption.expenditure.service.event;

public class ExpenditureMemberDeletedEvent {

    private final Long memberId;

    public ExpenditureMemberDeletedEvent(final Long memberId) {
        this.memberId = memberId;
    }

    public Long getMemberId() {
        return memberId;
    }
}

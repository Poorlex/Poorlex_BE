package com.poorlex.refactoring.expenditure.service.event;

public class ZeroExpenditureCreatedEvent {

    private final Long memberId;

    public ZeroExpenditureCreatedEvent(final Long memberId) {
        this.memberId = memberId;
    }

    public Long getMemberId() {
        return memberId;
    }
}

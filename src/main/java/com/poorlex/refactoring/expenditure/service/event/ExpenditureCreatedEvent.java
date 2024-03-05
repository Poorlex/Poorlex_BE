package com.poorlex.refactoring.expenditure.service.event;

public class ExpenditureCreatedEvent {

    private final Long memberId;

    public ExpenditureCreatedEvent(final Long memberId) {
        this.memberId = memberId;
    }

    public Long getMemberId() {
        return memberId;
    }
}

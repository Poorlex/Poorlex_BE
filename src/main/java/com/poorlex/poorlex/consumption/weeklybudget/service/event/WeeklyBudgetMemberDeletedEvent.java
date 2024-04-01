package com.poorlex.poorlex.consumption.weeklybudget.service.event;

public class WeeklyBudgetMemberDeletedEvent {

    private final Long memberId;

    public WeeklyBudgetMemberDeletedEvent(final Long memberId) {
        this.memberId = memberId;
    }

    public Long getMemberId() {
        return memberId;
    }
}

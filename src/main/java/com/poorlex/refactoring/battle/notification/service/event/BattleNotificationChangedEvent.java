package com.poorlex.refactoring.battle.notification.service.event;

public class BattleNotificationChangedEvent {

    private final Long battlId;
    private final Long memberId;

    public BattleNotificationChangedEvent(final Long battlId, final Long memberId) {
        this.battlId = battlId;
        this.memberId = memberId;
    }

    public Long getBattlId() {
        return battlId;
    }

    public Long getMemberId() {
        return memberId;
    }
}

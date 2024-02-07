package com.poorlex.poorlex.voting.vote.service.event;

public class VoteCreatedEvent {

    private final Long battleId;
    private final Long memberId;

    public VoteCreatedEvent(final Long battleId, final Long memberId) {
        this.battleId = battleId;
        this.memberId = memberId;
    }

    public Long getBattleId() {
        return battleId;
    }

    public Long getMemberId() {
        return memberId;
    }
}

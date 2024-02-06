package com.poorlex.poorlex.voting.votingpaper.service.event;

public class VotingPaperCreatedEvent {

    private final Long battleId;
    private final Long memberId;

    public VotingPaperCreatedEvent(final Long battleId, final Long memberId) {
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

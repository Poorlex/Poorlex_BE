package com.poorlex.refactoring.battle.history.service.dto;

public class ParticipantTotalExpenditureDto {

    private Long memberId;
    private Long sumExpenditure;

    public ParticipantTotalExpenditureDto(final Long memberId, final Long sumExpenditure) {
        this.memberId = memberId;
        this.sumExpenditure = sumExpenditure;
    }

    public Long getMemberId() {
        return memberId;
    }

    public Long getSumExpenditure() {
        return sumExpenditure;
    }
}

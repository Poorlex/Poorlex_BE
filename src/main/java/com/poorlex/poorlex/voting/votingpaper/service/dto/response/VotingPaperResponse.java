package com.poorlex.poorlex.voting.votingpaper.service.dto.response;

import com.poorlex.poorlex.alarm.battlealarm.service.dto.response.AbstractBattleAlarmResponse;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class VotingPaperResponse extends AbstractBattleAlarmResponse {

    private static final String ALARM_TYPE = "VOTING_PAPER";
    private final String voteName;
    private final long voteAmount;
    private final boolean isAgree;

    public VotingPaperResponse(final String voteName,
                               final long voteAmount,
                               final boolean isAgree,
                               final LocalDateTime createdAt) {
        super(ALARM_TYPE, createdAt);
        this.voteName = voteName;
        this.voteAmount = voteAmount;
        this.isAgree = isAgree;
    }
}

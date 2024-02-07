package com.poorlex.poorlex.voting.vote.service.dto.response;

import com.poorlex.poorlex.alarm.battlealarm.service.dto.response.AbstractBattleAlarmResponse;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class VoteResponse extends AbstractBattleAlarmResponse {

    private static final String ALARM_TYPE = "VOTE";
    
    private final Long voteId;
    private final String voteMakerNickname;
    private final String voteName;
    private final String voteStatus;
    private final long voteAmount;
    private final int voteAgreeCount;
    private final int voteDisagreeCount;

    public VoteResponse(final Long voteId,
                        final String voteMakerNickname,
                        final String voteName,
                        final String voteStatus,
                        final long voteAmount,
                        final int voteAgreeCount,
                        final int voteDisagreeCount,
                        final LocalDateTime createdAt) {
        super(ALARM_TYPE, createdAt);
        this.voteId = voteId;
        this.voteMakerNickname = voteMakerNickname;
        this.voteName = voteName;
        this.voteStatus = voteStatus;
        this.voteAmount = voteAmount;
        this.voteAgreeCount = voteAgreeCount;
        this.voteDisagreeCount = voteDisagreeCount;
    }
}

package com.poorlex.poorlex.voting.votingpaper.service.dto.response;

import com.poorlex.poorlex.common.AbstractCreatedAtResponse;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class VotingPaperResponse extends AbstractCreatedAtResponse {

    private final String voteName;
    private final long amount;
    private final boolean isAgree;

    public VotingPaperResponse(final String voteName,
                               final long amount,
                               final boolean isAgree,
                               final LocalDateTime createdAt) {
        super(createdAt);
        this.voteName = voteName;
        this.amount = amount;
        this.isAgree = isAgree;
    }
}

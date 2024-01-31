package com.poorlex.poorlex.voting.vote.service.dto.response;

import com.poorlex.poorlex.common.AbstractCreatedAtResponse;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class VoteResponse extends AbstractCreatedAtResponse {

    private final Long id;
    private final String nickname;
    private final String name;
    private final String status;
    private final int amount;
    private final int agreeCount;
    private final int disagreeCount;

    public VoteResponse(final Long id,
                        final String nickname,
                        final String name,
                        final String status,
                        final int amount,
                        final int agreeCount,
                        final int disagreeCount,
                        final LocalDateTime createdAt) {
        super(createdAt);
        this.id = id;
        this.nickname = nickname;
        this.name = name;
        this.status = status;
        this.amount = amount;
        this.agreeCount = agreeCount;
        this.disagreeCount = disagreeCount;
    }
}

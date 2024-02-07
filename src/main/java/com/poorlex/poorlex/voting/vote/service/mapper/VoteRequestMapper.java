package com.poorlex.poorlex.voting.vote.service.mapper;

import com.poorlex.poorlex.voting.vote.domain.Vote;
import com.poorlex.poorlex.voting.vote.domain.VoteAmount;
import com.poorlex.poorlex.voting.vote.domain.VoteDuration;
import com.poorlex.poorlex.voting.vote.domain.VoteDurationType;
import com.poorlex.poorlex.voting.vote.domain.VoteName;
import com.poorlex.poorlex.voting.vote.domain.VoteStatus;
import com.poorlex.poorlex.voting.vote.service.dto.request.VoteCreateRequest;

public class VoteRequestMapper {

    private VoteRequestMapper() {
    }

    public static Vote mapToVote(final Long battleId, final Long memberId, final VoteCreateRequest request) {
        final VoteAmount amount = new VoteAmount(request.getAmount());
        final VoteName name = new VoteName(request.getName());
        final VoteDuration duration = new VoteDuration(
            request.getStart(), VoteDurationType.findByMinute(request.getDuration())
            .orElseThrow(IllegalArgumentException::new)
        );

        return Vote.withoutId(battleId, memberId, amount, duration, name, VoteStatus.PROGRESS);
    }
}

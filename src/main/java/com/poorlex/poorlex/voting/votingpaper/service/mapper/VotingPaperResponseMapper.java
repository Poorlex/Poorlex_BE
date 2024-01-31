package com.poorlex.poorlex.voting.votingpaper.service.mapper;

import com.poorlex.poorlex.voting.votingpaper.domain.VotingPaper;
import com.poorlex.poorlex.voting.votingpaper.domain.VotingPaperWithVoteNameAndAmount;
import com.poorlex.poorlex.voting.votingpaper.service.dto.response.VotingPaperResponse;

public class VotingPaperResponseMapper {

    private VotingPaperResponseMapper() {

    }

    public static VotingPaperResponse mapToResponse(final VotingPaperWithVoteNameAndAmount votingPaperInfo) {
        final VotingPaper votingPaper = votingPaperInfo.getVotingPaper();
        final String voteName = votingPaperInfo.getVoteName();
        final long voteAmount = votingPaperInfo.getVoteAmount();

        return new VotingPaperResponse(voteName, voteAmount, votingPaper.isAgree(), votingPaper.getCreatedAt());
    }
}

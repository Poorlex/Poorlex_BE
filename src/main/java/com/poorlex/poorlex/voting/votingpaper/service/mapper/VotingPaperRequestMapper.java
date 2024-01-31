package com.poorlex.poorlex.voting.votingpaper.service.mapper;

import com.poorlex.poorlex.voting.votingpaper.domain.VotingPaper;
import com.poorlex.poorlex.voting.votingpaper.domain.VotingPaperType;
import com.poorlex.poorlex.voting.votingpaper.service.dto.request.VotingPaperCreateRequest;

public class VotingPaperRequestMapper {

    private VotingPaperRequestMapper() {

    }

    public static VotingPaper mapToVotingPaper(final Long memberId,
                                               final Long voteId,
                                               final VotingPaperCreateRequest request) {
        if (request.isAgree()) {
            return VotingPaper.withoutId(voteId, memberId, VotingPaperType.AGREE);
        }
        return VotingPaper.withoutId(voteId, memberId, VotingPaperType.DISAGREE);
    }
}

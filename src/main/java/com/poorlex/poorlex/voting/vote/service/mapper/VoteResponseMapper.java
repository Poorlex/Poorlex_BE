package com.poorlex.poorlex.voting.vote.service.mapper;

import com.poorlex.poorlex.voting.vote.domain.VoteAndVotingCountDto;
import com.poorlex.poorlex.voting.vote.service.dto.response.VoteResponse;
import java.time.LocalDateTime;

public class VoteResponseMapper {

    private VoteResponseMapper() {

    }

    public static VoteResponse mapToResponse(final VoteAndVotingCountDto voteAndVotingCountDto) {
        final Long id = voteAndVotingCountDto.getVoteId();
        final String voteMemberNickname = voteAndVotingCountDto.getVoteMemberNickname();
        final String voteName = voteAndVotingCountDto.getVoteName();
        final String status = voteAndVotingCountDto.getVoteStatus();
        final int amount = voteAndVotingCountDto.getVoteAmount();
        final int agreeCount = voteAndVotingCountDto.getAgreeVotingPaperCount();
        final int disagreeCount = voteAndVotingCountDto.getDisagreeVotingPaperCount();
        final LocalDateTime dateTime = voteAndVotingCountDto.getVoteStart();

        return new VoteResponse(id, voteMemberNickname, voteName, status, amount, agreeCount, disagreeCount, dateTime);
    }
}

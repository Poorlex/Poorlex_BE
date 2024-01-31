package com.poorlex.poorlex.voting.vote.domain;

import java.time.LocalDateTime;

public interface VoteAndVotingCountDto {

    Long getVoteId();

    Long getVoteBattleId();

    String getVoteMemberNickname();

    int getVoteAmount();

    LocalDateTime getVoteStart();

    String getVoteName();

    String getVoteStatus();

    int getAgreeVotingPaperCount();

    int getDisagreeVotingPaperCount();

}

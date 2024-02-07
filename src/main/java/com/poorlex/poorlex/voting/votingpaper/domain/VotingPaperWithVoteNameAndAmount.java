package com.poorlex.poorlex.voting.votingpaper.domain;

public interface VotingPaperWithVoteNameAndAmount {

    VotingPaper getVotingPaper();

    String getVoteName();

    long getVoteAmount();
}

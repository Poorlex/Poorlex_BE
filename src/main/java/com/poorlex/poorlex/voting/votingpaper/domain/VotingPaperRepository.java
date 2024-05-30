package com.poorlex.poorlex.voting.votingpaper.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VotingPaperRepository extends JpaRepository<VotingPaper, Long> {

    @Query("select vp as votingPaper, v.name.value as voteName, v.amount.value as voteAmount from VotingPaper vp "
        + "left join Vote v on v.id = vp.voteId and v.battleId = :battleId")
    List<VotingPaperWithVoteNameAndAmount> findVotingPapersByBattleId(@Param("battleId") final Long battleId);
}

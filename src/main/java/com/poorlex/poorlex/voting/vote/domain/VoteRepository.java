package com.poorlex.poorlex.voting.vote.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    @Query(value = "select v.id as voteId, "
        + "v.battle_id as voteBattleId, "
        + "m.nickname as voteMemberNickname, "
        + "v.amount as voteAmount, "
        + "v.start as voteStart, "
        + "v.name as voteName, "
        + "v.status as voteStatus, "
        + "count(case when vp.type='AGREE' then 1 end) as agreeVotingPaperCount, "
        + "count(case when vp.type='DISAGREE' then 1 end) as disagreeVotingPaperCount "
        + "from vote v "
        + "left join voting_paper vp on v.id = vp.vote_id "
        + "left join member m on m.id = v.member_id "
        + "where v.battle_id =  :battleId "
        + "group by v.id",
        nativeQuery = true)
    List<VoteAndVotingCountDto> findVotesByBattleId(@Param("battleId") final Long battleId);
}

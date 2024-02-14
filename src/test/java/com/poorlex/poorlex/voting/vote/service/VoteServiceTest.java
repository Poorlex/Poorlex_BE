package com.poorlex.poorlex.voting.vote.service;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.poorlex.poorlex.participate.domain.BattleParticipantRepository;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import com.poorlex.poorlex.support.db.UsingDataJpaTest;
import com.poorlex.poorlex.voting.vote.domain.Vote;
import com.poorlex.poorlex.voting.vote.domain.VoteAmount;
import com.poorlex.poorlex.voting.vote.domain.VoteDuration;
import com.poorlex.poorlex.voting.vote.domain.VoteDurationType;
import com.poorlex.poorlex.voting.vote.domain.VoteName;
import com.poorlex.poorlex.voting.vote.domain.VoteRepository;
import com.poorlex.poorlex.voting.vote.domain.VoteStatus;
import com.poorlex.poorlex.voting.vote.service.dto.response.VoteResponse;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class VoteServiceTest extends UsingDataJpaTest implements ReplaceUnderScoreTest {

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private BattleParticipantRepository battleParticipantRepository;

    private VoteService voteService;

    @BeforeEach
    void setUp() {
        this.voteService = new VoteService(voteRepository, battleParticipantRepository);
    }

    @Test
    void 배틀에서_생성된_투표를_조회한다() {
        //given
        final long memberId = 1L;
        final long battleId = 1L;

        final Vote vote1 = createVote(battleId, memberId, "초코우유");
        final Vote vote2 = createVote(battleId, memberId, "딸기우유");
        final Vote vote3 = createVote(battleId, memberId, "바나나우유");

        //when
        final List<VoteResponse> battleVotes = voteService.findBattleVotes(battleId);

        //then
        assertSoftly(
            softly -> {
                softly.assertThat(battleVotes).hasSize(3);
                softly.assertThat(battleVotes.get(0).getVoteName()).isEqualTo(vote1.getName());
                softly.assertThat(battleVotes.get(1).getVoteName()).isEqualTo(vote2.getName());
                softly.assertThat(battleVotes.get(2).getVoteName()).isEqualTo(vote3.getName());
            }
        );
    }

    private Vote createVote(final Long battleId, final Long memberId, final String voteName) {
        return voteRepository.save(Vote.withoutId(battleId,
            memberId,
            new VoteAmount(1000L),
            new VoteDuration(LocalDateTime.now(), VoteDurationType.SIXTY_MINUTE),
            new VoteName(voteName),
            VoteStatus.PROGRESS)
        );
    }
}

package com.poorlex.poorlex.voting.votingpaper.service;

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
import com.poorlex.poorlex.voting.votingpaper.domain.VotingPaper;
import com.poorlex.poorlex.voting.votingpaper.domain.VotingPaperRepository;
import com.poorlex.poorlex.voting.votingpaper.domain.VotingPaperType;
import com.poorlex.poorlex.voting.votingpaper.service.dto.response.VotingPaperResponse;
import java.time.LocalDateTime;
import java.util.List;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class VotingPaperServiceTest extends UsingDataJpaTest implements ReplaceUnderScoreTest {

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private VotingPaperRepository votingPaperRepository;

    @Autowired
    private BattleParticipantRepository battleParticipantRepository;

    private VotingPaperService votingPaperService;

    @BeforeEach
    void setUp() {
        this.votingPaperService = new VotingPaperService(
                voteRepository,
                votingPaperRepository,
                battleParticipantRepository
        );
        initializeDataBase();
    }

    @Test
    void 배틀에서_생성된_투표된_표를_조회한다() {
        //given
        final long memberId = 1L;
        final long battleId = 1L;

        final Vote vote1 = createVote(battleId, memberId, "초코우유");
        final Vote vote2 = createVote(battleId, memberId, "딸기우유");
        final Vote vote3 = createVote(battleId, memberId, "바나나우유");

        votingPaperRepository.save(VotingPaper.withoutId(vote1.getId(), memberId, VotingPaperType.AGREE));
        votingPaperRepository.save(VotingPaper.withoutId(vote2.getId(), memberId, VotingPaperType.DISAGREE));
        votingPaperRepository.save(VotingPaper.withoutId(vote3.getId(), memberId, VotingPaperType.AGREE));

        //when
        final List<VotingPaperResponse> battleVotingPapers = votingPaperService.findBattleVotingPapers(battleId);

        //then
        assertSoftly(
                softly -> {
                    softly.assertThat(battleVotingPapers).hasSize(3);
                    softly.assertThat(battleVotingPapers.get(0).getVoteName()).isEqualTo(vote1.getName());
                    softly.assertThat(battleVotingPapers.get(1).getVoteName()).isEqualTo(vote2.getName());
                    softly.assertThat(battleVotingPapers.get(2).getVoteName()).isEqualTo(vote3.getName());
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

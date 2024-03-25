package com.poorlex.poorlex.voting.vote.domain;

import com.poorlex.poorlex.user.member.domain.Member;
import com.poorlex.poorlex.user.member.domain.MemberNickname;
import com.poorlex.poorlex.user.member.domain.MemberRepository;
import com.poorlex.poorlex.user.member.domain.Oauth2RegistrationId;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import com.poorlex.poorlex.support.db.UsingDataJpaTest;
import com.poorlex.poorlex.voting.votingpaper.domain.VotingPaper;
import com.poorlex.poorlex.voting.votingpaper.domain.VotingPaperRepository;
import com.poorlex.poorlex.voting.votingpaper.domain.VotingPaperType;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class VoteRepositoryTest extends UsingDataJpaTest implements ReplaceUnderScoreTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private VoteRepository voteRepository;
    @Autowired
    private VotingPaperRepository votingPaperRepository;

    @Test
    void find() {
        //given
        final Member member = memberRepository.save(
                Member.withoutId(Oauth2RegistrationId.APPLE, "oauthId", new MemberNickname("nickname1")));
        final Member member2 = memberRepository.save(
                Member.withoutId(Oauth2RegistrationId.APPLE, "oauthId2", new MemberNickname("nickname2")));
        final long battleId = 1L;
        final Vote vote = voteRepository.save(
                Vote.withoutId(
                        battleId,
                        member.getId(),
                        new VoteAmount(10000),
                        new VoteDuration(LocalDateTime.now(), VoteDurationType.TEN_MINUTE),
                        new VoteName("voteName"),
                        VoteStatus.PROGRESS
                )
        );
        final Vote vote2 = voteRepository.save(
                Vote.withoutId(
                        battleId,
                        member2.getId(),
                        new VoteAmount(20000),
                        new VoteDuration(LocalDateTime.now(), VoteDurationType.TEN_MINUTE),
                        new VoteName("voteName"),
                        VoteStatus.FINISHED
                )
        );
        votingPaperRepository.save(VotingPaper.withoutId(vote.getId(), member.getId(), VotingPaperType.AGREE));
        votingPaperRepository.save(VotingPaper.withoutId(vote.getId(), member2.getId(), VotingPaperType.DISAGREE));

        votingPaperRepository.save(VotingPaper.withoutId(vote2.getId(), member2.getId(), VotingPaperType.DISAGREE));

        //when
        final List<VoteAndVotingCountDto> votes = voteRepository.findVotesByBattleId(battleId);
        votes.sort(Comparator.comparing(VoteAndVotingCountDto::getVoteStart));

        //then
        assertSoftly(
                softly -> {
                    final VoteAndVotingCountDto firstVote = votes.get(0);
                    softly.assertThat(firstVote.getVoteId()).isEqualTo(vote.getId());
                    softly.assertThat(firstVote.getVoteBattleId()).isEqualTo(vote.getBattleId());
                    softly.assertThat(firstVote.getVoteName()).isEqualTo(vote.getName());
                    softly.assertThat(firstVote.getVoteAmount()).isEqualTo(vote.getAmount());
                    softly.assertThat(firstVote.getAgreeVotingPaperCount()).isEqualTo(1);
                    softly.assertThat(firstVote.getDisagreeVotingPaperCount()).isEqualTo(1);
                    softly.assertThat(firstVote.getVoteStatus()).isEqualTo(vote.getStatus().name());
                    softly.assertThat(firstVote.getVoteMemberNickname()).isEqualTo(member.getNickname());
                    softly.assertThat(firstVote.getVoteStart()).isEqualTo(vote.getStart());

                    final VoteAndVotingCountDto secondVote = votes.get(1);
                    softly.assertThat(secondVote.getVoteId()).isEqualTo(vote2.getId());
                    softly.assertThat(secondVote.getVoteBattleId()).isEqualTo(vote2.getBattleId());
                    softly.assertThat(secondVote.getVoteName()).isEqualTo(vote2.getName());
                    softly.assertThat(secondVote.getVoteAmount()).isEqualTo(vote2.getAmount());
                    softly.assertThat(secondVote.getAgreeVotingPaperCount()).isEqualTo(0);
                    softly.assertThat(secondVote.getDisagreeVotingPaperCount()).isEqualTo(1);
                    softly.assertThat(secondVote.getVoteStatus()).isEqualTo(vote2.getStatus().name());
                    softly.assertThat(secondVote.getVoteMemberNickname()).isEqualTo(member2.getNickname());
                    softly.assertThat(secondVote.getVoteStart()).isEqualTo(vote2.getStart());
                }
        );
    }
}

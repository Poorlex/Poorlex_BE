package com.poorlex.poorlex.voting.votingpaper.service;

import com.poorlex.poorlex.voting.vote.domain.Vote;
import com.poorlex.poorlex.voting.vote.domain.VoteRepository;
import com.poorlex.poorlex.voting.votingpaper.domain.VotingPaper;
import com.poorlex.poorlex.voting.votingpaper.domain.VotingPaperRepository;
import com.poorlex.poorlex.voting.votingpaper.service.dto.request.VotingPaperCreateRequest;
import com.poorlex.poorlex.voting.votingpaper.service.dto.response.VotingPaperResponse;
import com.poorlex.poorlex.voting.votingpaper.service.mapper.VotingPaperRequestMapper;
import com.poorlex.poorlex.voting.votingpaper.service.mapper.VotingPaperResponseMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VotingPaperService {

    private final VoteRepository voteRepository;
    private final VotingPaperRepository votingPaperRepository;

    @Transactional
    public void createVotingPaper(final Long memberId, final Long voteId, final VotingPaperCreateRequest request) {
        final Vote vote = voteRepository.findById(voteId)
            .orElseThrow(() -> new IllegalArgumentException("Id에 해당하는 투표가 존재하지 않습니다."));
        if (vote.isFinished()) {
            throw new IllegalArgumentException("이미 끝난 투표입니다.");
        }
        final VotingPaper votingPaper = VotingPaperRequestMapper.mapToVotingPaper(memberId, voteId, request);
        votingPaperRepository.save(votingPaper);
    }

    public List<VotingPaperResponse> findBattleVotingPapers(final Long battleId) {
        return votingPaperRepository.findVotingPapersByBattleId(battleId).stream()
            .map(VotingPaperResponseMapper::mapToResponse)
            .toList();
    }
}

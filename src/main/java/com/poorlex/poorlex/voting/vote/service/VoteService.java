package com.poorlex.poorlex.voting.vote.service;

import com.poorlex.poorlex.voting.vote.domain.VoteRepository;
import com.poorlex.poorlex.voting.vote.service.dto.request.VoteCreateRequest;
import com.poorlex.poorlex.voting.vote.service.dto.response.VoteResponse;
import com.poorlex.poorlex.voting.vote.service.mapper.VoteRequestMapper;
import com.poorlex.poorlex.voting.vote.service.mapper.VoteResponseMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;

    public void createVote(final Long memberId, final Long battleId, final VoteCreateRequest request) {
        voteRepository.save(VoteRequestMapper.mapToVote(memberId, battleId, request));
    }

    public List<VoteResponse> findBattleVotes(final Long battleId) {
        return voteRepository.findVotesByBattleId(battleId).stream()
            .map(VoteResponseMapper::mapToResponse)
            .toList();
    }
}

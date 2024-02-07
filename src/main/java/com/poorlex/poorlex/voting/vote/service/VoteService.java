package com.poorlex.poorlex.voting.vote.service;

import com.poorlex.poorlex.config.event.Events;
import com.poorlex.poorlex.participate.domain.BattleParticipantRepository;
import com.poorlex.poorlex.voting.vote.domain.VoteRepository;
import com.poorlex.poorlex.voting.vote.service.dto.request.VoteCreateRequest;
import com.poorlex.poorlex.voting.vote.service.dto.response.VoteResponse;
import com.poorlex.poorlex.voting.vote.service.event.VoteCreatedEvent;
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
    private final BattleParticipantRepository battleParticipantRepository;

    public void createVote(final Long memberId, final Long battleId, final VoteCreateRequest request) {
        validateBattleParticipant(memberId, battleId);
        voteRepository.save(VoteRequestMapper.mapToVote(memberId, battleId, request));
        Events.raise(new VoteCreatedEvent(battleId, memberId));
    }

    private void validateBattleParticipant(final Long memberId, final Long battleId) {
        if (!battleParticipantRepository.existsByBattleIdAndMemberId(battleId, memberId)) {
            throw new IllegalArgumentException("배틀의 참가자가 아닙니다.");
        }
    }

    public List<VoteResponse> findBattleVotes(final Long battleId) {
        return voteRepository.findVotesByBattleId(battleId).stream()
            .map(VoteResponseMapper::mapToResponse)
            .toList();
    }
}

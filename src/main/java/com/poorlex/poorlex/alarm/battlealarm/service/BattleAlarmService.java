package com.poorlex.poorlex.alarm.battlealarm.service;

import com.poorlex.poorlex.alarm.battlealarm.domain.BattleAlarmRepository;
import com.poorlex.poorlex.alarm.battlealarm.domain.BattleAlarmViewHistory;
import com.poorlex.poorlex.alarm.battlealarm.domain.BattleAlarmViewHistoryRepository;
import com.poorlex.poorlex.alarm.battlealarm.service.dto.request.BattleAlarmRequest;
import com.poorlex.poorlex.alarm.battlealarm.service.dto.response.AbstractBattleAlarmResponse;
import com.poorlex.poorlex.alarm.battlealarm.service.dto.response.BattleAlarmResponse;
import com.poorlex.poorlex.alarm.battlealarm.service.dto.response.UncheckedBattleAlarmCountResponse;
import com.poorlex.poorlex.alarm.battlealarm.service.event.BattleAlarmViewedEvent;
import com.poorlex.poorlex.battlealarmreaction.service.AlarmReactionService;
import com.poorlex.poorlex.config.event.Events;
import com.poorlex.poorlex.voting.vote.service.VoteService;
import com.poorlex.poorlex.voting.votingpaper.service.VotingPaperService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BattleAlarmService {

    private final BattleAlarmRepository battleAlarmRepository;
    private final VoteService voteService;
    private final VotingPaperService votingPaperService;
    private final AlarmReactionService alarmReactionService;
    private final BattleAlarmViewHistoryRepository battleAlarmViewHistoryRepository;

    public List<AbstractBattleAlarmResponse> findBattleAlarms(final Long battleId,
                                                              final Long memberId,
                                                              final BattleAlarmRequest request) {
        final List<AbstractBattleAlarmResponse> responses = new ArrayList<>();
        responses.addAll(BattleAlarmResponse.mapToList(battleAlarmRepository.findAllByBattleId(battleId)));
        responses.addAll(voteService.findBattleVotes(battleId));
        responses.addAll(votingPaperService.findBattleVotingPapers(battleId));
        responses.addAll(alarmReactionService.findBattleAlarmReactions(battleId));
        responses.sort(Comparator.comparing(AbstractBattleAlarmResponse::getCreatedAt));
        Events.raise(new BattleAlarmViewedEvent(battleId, memberId, request.getDateTime()));
        return responses;
    }

    public UncheckedBattleAlarmCountResponse getBattleParticipantUncheckedAlarmCount(final Long battleId,
                                                                                     final Long memberId) {
        final Optional<BattleAlarmViewHistory> viewHistory =
            battleAlarmViewHistoryRepository.findByBattleIdAndMemberId(battleId, memberId);

        return viewHistory.map(history -> getResponseHistoryExist(battleId, memberId, history.getLastViewTime()))
            .orElseGet(() -> getResponseHistoryNotExist(battleId, memberId));
    }

    private UncheckedBattleAlarmCountResponse getResponseHistoryNotExist(final Long battleId, final Long memberId) {
        final int uncheckedCount = battleAlarmRepository.countBattleAlarmByBattleIdAndMemberId(battleId, memberId);
        return new UncheckedBattleAlarmCountResponse(uncheckedCount);
    }

    private UncheckedBattleAlarmCountResponse getResponseHistoryExist(final Long battleId,
                                                                      final Long memberId,
                                                                      final LocalDateTime lastViewTime) {
        final int uncheckedAlarmCount = battleAlarmRepository.countBattleAlarmByBattleIdAndMemberIdAndCreatedAtAfter(
            battleId,
            memberId,
            lastViewTime
        );

        return new UncheckedBattleAlarmCountResponse(uncheckedAlarmCount);
    }
}

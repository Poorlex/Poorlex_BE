package com.poorlex.refactoring.battle.battle.service;

import com.poorlex.refactoring.battle.battle.domain.Battle;
import com.poorlex.refactoring.battle.battle.domain.BattleRepository;
import com.poorlex.refactoring.battle.battle.domain.BattleStatus;
import com.poorlex.refactoring.battle.battle.domain.BattleWithCurrentParticipantSize;
import com.poorlex.refactoring.battle.battle.domain.ExpenditureRankings;
import com.poorlex.refactoring.battle.battle.service.dto.BattleHistoryDto;
import com.poorlex.refactoring.battle.battle.service.dto.BattleParticipantDto;
import com.poorlex.refactoring.battle.battle.service.dto.BattleParticipantNicknameAndLevelDto;
import com.poorlex.refactoring.battle.battle.service.dto.BattleParticipantWithExpenditureDto;
import com.poorlex.refactoring.battle.battle.service.dto.request.BattleFindRequest;
import com.poorlex.refactoring.battle.battle.service.dto.response.BattleAndCurrentParticipantSizeResponse;
import com.poorlex.refactoring.battle.battle.service.dto.response.BattleParticipantResponse;
import com.poorlex.refactoring.battle.battle.service.dto.response.BattleResponse;
import com.poorlex.refactoring.battle.battle.service.dto.response.BattleSpecificResponse;
import com.poorlex.refactoring.battle.battle.service.dto.response.MemberCompleteBattleResponse;
import com.poorlex.refactoring.battle.battle.service.dto.response.MemberProgressBattleResponse;
import com.poorlex.refactoring.battle.battle.service.provider.BattleParticipantCountProvider;
import com.poorlex.refactoring.battle.battle.service.provider.BattleParticipantMemberIdAndRoleProvider;
import com.poorlex.refactoring.battle.battle.service.provider.BattleParticipantNickNameAndLevelProvider;
import com.poorlex.refactoring.battle.battle.service.provider.BattleParticipantSumExpenditureProvider;
import com.poorlex.refactoring.battle.battle.service.provider.BattleUncheckedAlarmCountProvider;
import com.poorlex.refactoring.battle.battle.service.provider.CompleteBattleHistoryProvider;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BattleQueryService {

    private final BattleRepository battleRepository;
    private final CompleteBattleHistoryProvider completeBattleHistoryProvider;
    private final BattleParticipantCountProvider battleParticipantCountProvider;
    private final BattleUncheckedAlarmCountProvider battleUncheckedAlarmCountProvider;
    private final BattleParticipantSumExpenditureProvider battleParticipantSumExpenditureProvider;
    private final BattleParticipantMemberIdAndRoleProvider battleParticipantMemberIdAndRoleProvider;
    private final BattleParticipantNickNameAndLevelProvider battleParticipantNickNameAndLevelProvider;

    public List<BattleResponse> findBattlesToPlay() {
        final List<BattleWithCurrentParticipantSize> battlesByMemberIdWithCurrentParticipantSize =
            battleRepository.findBattlesWithCurrentParticipantSizeByStatusesIn(BattleStatus.getReadyStatues());
        final List<BattleResponse> responses = new ArrayList<>();
        for (BattleWithCurrentParticipantSize battleInfo : battlesByMemberIdWithCurrentParticipantSize) {
            responses.add(BattleAndCurrentParticipantSizeResponse.from(battleInfo));
        }
        return responses;
    }

    public List<BattleResponse> findBattleByStatusWithCurrentDate(final Long memberId, final String status) {
        final BattleStatus battleStatus = BattleStatus.findByName(status);
        return findBattleByStatusAndDate(memberId, battleStatus, LocalDate.now());
    }

    public List<BattleResponse> findBattleByStatusAndDate(final Long memberId,
                                                          final BattleStatus battleStatus,
                                                          final LocalDate date) {
        if (battleStatus == BattleStatus.PROGRESS) {
            return findProgressMemberBattles(memberId, date);
        }
        if (battleStatus == BattleStatus.COMPLETE) {
            return findCompleteMemberBattles(memberId, date);
        }
        return findBattlesToPlay();
    }

    public List<BattleResponse> findProgressMemberBattles(final Long memberId, final LocalDate date) {
        final List<Battle> battles =
            battleRepository.findMemberBattlesByMemberIdAndStatus(memberId, BattleStatus.PROGRESS);
        return battles.stream()
            .map(battle -> mapToProgressBattleResponse(battle, memberId, date))
            .toList();
    }

    private BattleResponse mapToProgressBattleResponse(final Battle battle,
                                                       final Long memberId,
                                                       final LocalDate date) {
        final Long battleId = battle.getId();
        final int participantCount = battleParticipantCountProvider.getByBattleId(battleId);
        final int uncheckedAlarmCount = battleUncheckedAlarmCountProvider.getByBattleIdAndMemberId(battleId, memberId);

        return new MemberProgressBattleResponse(battle, battle.getNumberOfDaysBeforeEnd(date), participantCount,
            uncheckedAlarmCount);
    }

    public List<BattleResponse> findCompleteMemberBattles(final Long memberId, final LocalDate date) {
        return completeBattleHistoryProvider.getByMemberId(memberId).stream()
            .map(battleHistory -> mapToCompleteBattleResponse(battleHistory, date))
            .toList();
    }

    private BattleResponse mapToCompleteBattleResponse(final BattleHistoryDto battleHistory, final LocalDate date) {
        return MemberCompleteBattleResponse.from(battleHistory, date);
    }

    public BattleSpecificResponse findBattle(final Long battleId, final BattleFindRequest request) {
        final Battle battle = battleRepository.findById(battleId)
            .orElseThrow(IllegalArgumentException::new);
        final List<BattleParticipantWithExpenditureDto> participants = getParticipantsWithExpenditure(battle);
        final ExpenditureRankings ranking = ExpenditureRankings.from(participants);
        final List<BattleParticipantResponse> participantResponses = getResponseSortedByRank(participants, ranking);

        return BattleSpecificResponse.of(battle, request.getDate(), participantResponses);
    }

    private List<BattleParticipantWithExpenditureDto> getParticipantsWithExpenditure(final Battle battle) {
        final List<BattleParticipantDto> participants =
            battleParticipantMemberIdAndRoleProvider.getByBattleId(battle.getId());
        final LocalDateTime start = battle.getStart();
        final LocalDateTime end = battle.getEnd();

        return participants.stream()
            .map(participant -> getBattleParticipantWithExpenditureDto(participant, start, end))
            .toList();
    }

    private BattleParticipantWithExpenditureDto getBattleParticipantWithExpenditureDto(
        final BattleParticipantDto participant,
        final LocalDateTime start,
        final LocalDateTime end
    ) {
        final Long battleParticipantSumExpenditure =
            battleParticipantSumExpenditureProvider.byMemberIdBetween(participant.getMemberId(), start, end);
        final BattleParticipantNicknameAndLevelDto nicknameAndLevel =
            battleParticipantNickNameAndLevelProvider.byMemberId(participant.getMemberId());

        return new BattleParticipantWithExpenditureDto(
            participant.getMemberId(),
            participant.getRole(),
            battleParticipantSumExpenditure,
            nicknameAndLevel.getNickname(),
            nicknameAndLevel.getLevel()
        );
    }

    private List<BattleParticipantResponse> getResponseSortedByRank(
        final List<BattleParticipantWithExpenditureDto> participants,
        final ExpenditureRankings ranking
    ) {
        return participants.stream()
            .map(
                participant ->
                    BattleParticipantResponse.from(participant, ranking.getByMemberId(participant.getMemberId()))
            )
            .sorted(Comparator.comparingInt(BattleParticipantResponse::getRank))
            .toList();
    }
}

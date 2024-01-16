package com.poolex.poolex.battle.service;

import com.poolex.poolex.alarm.service.AlarmService;
import com.poolex.poolex.battle.domain.Battle;
import com.poolex.poolex.battle.domain.BattleParticipantWithExpenditure;
import com.poolex.poolex.battle.domain.BattleRepository;
import com.poolex.poolex.battle.domain.BattleStatus;
import com.poolex.poolex.battle.domain.BattleWithCurrentParticipantSize;
import com.poolex.poolex.battle.domain.BattleWithMemberExpenditure;
import com.poolex.poolex.battle.service.dto.request.BattleCreateRequest;
import com.poolex.poolex.battle.service.dto.request.BattleFindRequest;
import com.poolex.poolex.battle.service.dto.response.BattleResponse;
import com.poolex.poolex.battle.service.dto.response.FindingBattleResponse;
import com.poolex.poolex.battle.service.dto.response.MemberCompleteBattleResponse;
import com.poolex.poolex.battle.service.dto.response.MemberProgressBattleResponse;
import com.poolex.poolex.battle.service.dto.response.ParticipantRankingResponse;
import com.poolex.poolex.battle.service.event.BattleCreatedEvent;
import com.poolex.poolex.battle.service.mapper.BattleMapper;
import com.poolex.poolex.config.event.Events;
import com.poolex.poolex.expenditure.service.ExpenditureService;
import com.poolex.poolex.expenditure.service.dto.RankAndTotalExpenditureDto;
import com.poolex.poolex.member.domain.MemberLevel;
import com.poolex.poolex.member.service.MemberService;
import com.poolex.poolex.participate.domain.BattleParticipant;
import com.poolex.poolex.participate.domain.BattleParticipantRepository;
import com.poolex.poolex.point.domain.Point;
import com.poolex.poolex.point.service.MemberPointService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BattleService {

    private final BattleRepository battleRepository;
    private final BattleParticipantRepository battleParticipantRepository;
    private final AlarmService alarmService;
    private final MemberPointService memberPointService;
    private final ExpenditureService expenditureService;
    private final MemberService memberService;

    @Transactional
    public Long create(final Long memberId, final BattleCreateRequest request) {
        final Battle battle = BattleMapper.createRequestToBattle(request);
        battleRepository.save(battle);
        Events.raise(new BattleCreatedEvent(battle.getId(), memberId));
        return battle.getId();
    }

    public List<FindingBattleResponse> findBattlesToPlay() {
        final List<BattleStatus> statuses = List.of(BattleStatus.RECRUITING, BattleStatus.RECRUITING_FINISHED);

        final List<BattleWithCurrentParticipantSize> battlesByMemberIdWithCurrentParticipantSize =
            battleRepository.findBattlesByStatusesWithCurrentParticipantSizeIn(statuses);

        return FindingBattleResponse.parseToList(battlesByMemberIdWithCurrentParticipantSize);
    }

    public List<MemberProgressBattleResponse> findProgressMemberBattles(final Long memberId, final LocalDate date) {
        final List<BattleWithMemberExpenditure> battles =
            battleRepository.findMemberBattlesByMemberIdAndStatusWithExpenditure(memberId, BattleStatus.PROGRESS);

        return battles.stream()
            .map(battleInfo -> mapToProgressBattleResponse(battleInfo, memberId, date))
            .toList();
    }

    private MemberProgressBattleResponse mapToProgressBattleResponse(final BattleWithMemberExpenditure battleInfo,
                                                                     final Long memberId,
                                                                     final LocalDate date) {
        final Battle battle = battleInfo.getBattle();
        final Long battleId = battle.getId();
        final int battleParticipantCount = battleParticipantRepository.countBattleParticipantByBattleId(battleId);
        final int uncheckedAlarmCount = alarmService.getBattleParticipantUncheckedAlarmCount(battleId, memberId)
            .getCount();

        return MemberProgressBattleResponse.from(
            battleInfo,
            battle.getDDay(date),
            getMemberRank(battle, memberId),
            battleParticipantCount,
            uncheckedAlarmCount
        );
    }

    private int getMemberRank(final Battle battle, final Long targetMemberId) {
        //책임 분리 리펙토링 필요
        final List<BattleParticipantWithExpenditure> battleParticipantsWithExpenditure =
            battleRepository.findBattleParticipantsWithExpenditureByBattleId(battle.getId())
                .stream()
                .sorted(Comparator.comparingInt(BattleParticipantWithExpenditure::getExpenditure))
                .toList();

        int rank = 0;
        int prevExpenditure = 0;
        int duplicateCount = 1;
        for (int idx = 0; idx < battleParticipantsWithExpenditure.size(); idx++) {
            final BattleParticipantWithExpenditure current = battleParticipantsWithExpenditure.get(idx);
            final int currentExpenditure = current.getExpenditure();

            if (idx == 0) {
                rank++;
            } else if (currentExpenditure == prevExpenditure) {
                duplicateCount++;
            } else if (currentExpenditure > prevExpenditure) {
                rank += duplicateCount;
                duplicateCount = 1;
            }

            if (current.getBattleParticipant().getMemberId().equals(targetMemberId)) {
                return rank;
            }
            prevExpenditure = currentExpenditure;
        }
        throw new IllegalArgumentException("배틀에 해당하는 멤버가 존재하지 않습니다.");
    }

    public List<MemberCompleteBattleResponse> findCompleteMemberBattles(final Long memberId, final LocalDate date) {
        final List<BattleWithMemberExpenditure> battles =
            battleRepository.findMemberBattlesByMemberIdAndStatusWithExpenditure(memberId, BattleStatus.COMPLETE);

        return battles.stream()
            .map(battleInfo -> mapToMemberCompleteBattleResponse(battleInfo, memberId, date))
            .toList();
    }

    private MemberCompleteBattleResponse mapToMemberCompleteBattleResponse(final BattleWithMemberExpenditure battleInfo,
                                                                           final Long memberId,
                                                                           final LocalDate date) {
        final Battle battle = battleInfo.getBattle();
        final int memberRank = getMemberRank(battle, memberId);
        final int battleParticipantCount = battleParticipantRepository.countBattleParticipantByBattleId(
            battle.getId());

        return MemberCompleteBattleResponse.from(battleInfo, date, memberRank, battleParticipantCount);
    }

    @Transactional
    public void startBattle(final Long battleId, final LocalDateTime current) {
        final Battle battle = battleRepository.findById(battleId)
            .orElseThrow(IllegalArgumentException::new);

        battle.start(current);
    }

    @Transactional
    public void endBattle(final Long battleId, final LocalDateTime current) {
        final Battle battle = battleRepository.findById(battleId)
            .orElseThrow(IllegalArgumentException::new);

        battle.end(current);
    }

    public BattleResponse getBattleInfo(final Long battleId, final BattleFindRequest request) {
        final Battle battle = battleRepository.findById(battleId)
            .orElseThrow(IllegalArgumentException::new);

        final List<BattleParticipant> participants = battleParticipantRepository.findAllByBattleId(battleId);
        final List<Long> participantMemberIds = participants.stream()
            .map(BattleParticipant::getMemberId)
            .toList();

        final Map<Long, String> participantsNickname = getParticipantsNickname(participantMemberIds);
        final Map<Long, Integer> participantsTotalPoint = getParticipantsTotalPoint(participantMemberIds);
        final List<ParticipantRankingResponse> sortedRankingsResponses = mapToParticipantRankingResponses(
            participants,
            participantsNickname,
            participantsTotalPoint,
            getParticipantsRanks(battle, participantMemberIds)
        );

        return new BattleResponse(battle, battle.getDDay(request.getDate()), sortedRankingsResponses);
    }

    private Map<Long, String> getParticipantsNickname(final List<Long> memberIds) {
        return memberService.getMembersNickname(memberIds);
    }

    private Map<Long, Integer> getParticipantsTotalPoint(final List<Long> memberIds) {
        return memberPointService.findMembersTotalPoint(memberIds);
    }

    private Map<Long, RankAndTotalExpenditureDto> getParticipantsRanks(final Battle battle,
                                                                       final List<Long> memberIds) {
        return expenditureService.getMembersTotalExpenditureRankBetween(
            memberIds,
            battle.getDuration().getStart(),
            battle.getDuration().getEnd()
        );
    }

    private List<ParticipantRankingResponse> mapToParticipantRankingResponses(
        final List<BattleParticipant> battleParticipants,
        final Map<Long, String> participantsNickname,
        final Map<Long, Integer> participantsTotalPoint,
        final Map<Long, RankAndTotalExpenditureDto> participantsRanks
    ) {
        return battleParticipants.stream()
            .map(battleParticipant -> {
                final Long memberId = battleParticipant.getMemberId();
                final RankAndTotalExpenditureDto rankInfo = participantsRanks.get(memberId);

                return mapToParticipantRankingResponse(
                    battleParticipant,
                    participantsNickname.get(memberId),
                    participantsTotalPoint.get(memberId),
                    rankInfo
                );
            }).sorted(Comparator.comparingInt(ParticipantRankingResponse::getRank))
            .toList();
    }

    private ParticipantRankingResponse mapToParticipantRankingResponse(final BattleParticipant battleParticipant,
                                                                       final String nickname,
                                                                       final int totalPoint,
                                                                       final RankAndTotalExpenditureDto rankInfo) {
        final int level = MemberLevel.findByPoint(new Point(totalPoint))
            .orElseThrow(IllegalArgumentException::new)
            .getNumber();

        return new ParticipantRankingResponse(
            rankInfo.getRank(),
            level,
            battleParticipant.isManager(),
            nickname,
            rankInfo.getTotalExpenditure()
        );
    }
}

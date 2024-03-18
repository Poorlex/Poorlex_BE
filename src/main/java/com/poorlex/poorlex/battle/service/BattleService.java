package com.poorlex.poorlex.battle.service;

import com.poorlex.poorlex.alarm.battlealarm.service.BattleAlarmService;
import com.poorlex.poorlex.battle.domain.Battle;
import com.poorlex.poorlex.battle.domain.BattleBudget;
import com.poorlex.poorlex.battle.domain.BattleDuration;
import com.poorlex.poorlex.battle.domain.BattleImageUrl;
import com.poorlex.poorlex.battle.domain.BattleIntroduction;
import com.poorlex.poorlex.battle.domain.BattleName;
import com.poorlex.poorlex.battle.domain.BattleParticipantSize;
import com.poorlex.poorlex.battle.domain.BattleParticipantWithExpenditure;
import com.poorlex.poorlex.battle.domain.BattleRepository;
import com.poorlex.poorlex.battle.domain.BattleStatus;
import com.poorlex.poorlex.battle.domain.BattleWithCurrentParticipantSize;
import com.poorlex.poorlex.battle.domain.BattleWithMemberExpenditure;
import com.poorlex.poorlex.battle.service.dto.request.BattleCreateRequest;
import com.poorlex.poorlex.battle.service.dto.request.BattleFindRequest;
import com.poorlex.poorlex.battle.service.dto.response.BattleResponse;
import com.poorlex.poorlex.battle.service.dto.response.FindingBattleResponse;
import com.poorlex.poorlex.battle.service.dto.response.MemberCompleteBattleResponse;
import com.poorlex.poorlex.battle.service.dto.response.MemberProgressBattleResponse;
import com.poorlex.poorlex.battle.service.dto.response.ParticipantRankingResponse;
import com.poorlex.poorlex.battle.service.event.BattleCreatedEvent;
import com.poorlex.poorlex.config.aws.AWSS3Service;
import com.poorlex.poorlex.config.event.Events;
import com.poorlex.poorlex.expenditure.service.ExpenditureQueryService;
import com.poorlex.poorlex.expenditure.service.dto.RankAndTotalExpenditureDto;
import com.poorlex.poorlex.member.domain.MemberLevel;
import com.poorlex.poorlex.member.service.MemberService;
import com.poorlex.poorlex.participate.domain.BattleParticipant;
import com.poorlex.poorlex.participate.domain.BattleParticipantRepository;
import com.poorlex.poorlex.point.domain.Point;
import com.poorlex.poorlex.point.service.MemberPointService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
public class BattleService {

    private static final int MAX_READIED_BATTLE_COUNT = 3;

    private final BattleRepository battleRepository;
    private final BattleParticipantRepository battleParticipantRepository;
    private final BattleAlarmService battleAlarmService;
    private final MemberPointService memberPointService;
    private final ExpenditureQueryService expenditureQueryService;
    private final MemberService memberService;
    private final AWSS3Service awss3Service;
    private final String bucketDirectory;

    public BattleService(final BattleRepository battleRepository,
                         final BattleParticipantRepository battleParticipantRepository,
                         final BattleAlarmService battleAlarmService,
                         final MemberPointService memberPointService,
                         final ExpenditureQueryService expenditureQueryService,
                         final MemberService memberService,
                         final AWSS3Service awss3Service,
                         @Value("${aws.s3.battle-directory}") final String bucketDirectory) {
        this.battleRepository = battleRepository;
        this.battleParticipantRepository = battleParticipantRepository;
        this.battleAlarmService = battleAlarmService;
        this.memberPointService = memberPointService;
        this.expenditureQueryService = expenditureQueryService;
        this.memberService = memberService;
        this.awss3Service = awss3Service;
        this.bucketDirectory = bucketDirectory;
    }

    @Transactional
    public Long create(final Long memberId, final MultipartFile image, final BattleCreateRequest request) {
        validateMemberCanCreateBattle(memberId);
        final Battle battle = battleRepository.save(createBattle(image, request));

        Events.raise(new BattleCreatedEvent(battle.getId(), memberId));

        return battle.getId();
    }

    private void validateMemberCanCreateBattle(final Long memberId) {
        final int readiedBattleCount = battleRepository.countMemberBattleWithStatuses(
                memberId,
                BattleStatus.getReadiedStatues()
        );
        if (readiedBattleCount >= MAX_READIED_BATTLE_COUNT) {
            throw new IllegalArgumentException("배틀은 최대 3개까지 참여할 수 있습니다.");
        }
    }

    private Battle createBattle(final MultipartFile image, final BattleCreateRequest request) {
        final BattleName name = new BattleName(request.getName());
        final BattleIntroduction introduction = new BattleIntroduction(request.getIntroduction());
        final BattleBudget budget = new BattleBudget(request.getBudget());
        final BattleParticipantSize participantSize = new BattleParticipantSize(request.getMaxParticipantSize());
        final String imageUrl = awss3Service.uploadMultipartFile(image, bucketDirectory);
        return Battle.withoutBattleId(name,
                                      introduction,
                                      new BattleImageUrl(imageUrl),
                                      budget,
                                      participantSize,
                                      BattleDuration.current(),
                                      BattleStatus.RECRUITING);
    }

    public List<FindingBattleResponse> findBattlesToPlay() {
        final List<BattleStatus> statuses = List.of(BattleStatus.RECRUITING, BattleStatus.RECRUITING_FINISHED);

        final List<BattleWithCurrentParticipantSize> battlesByMemberIdWithCurrentParticipantSize =
                battleRepository.findBattlesWithCurrentParticipantSizeByStatusesIn(statuses);

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
        final int uncheckedAlarmCount = battleAlarmService.getBattleParticipantUncheckedAlarmCount(battleId, memberId)
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
                        .sorted(Comparator.comparingLong(BattleParticipantWithExpenditure::getExpenditure))
                        .toList();

        int rank = 0;
        Long prevExpenditure = -1L;
        int duplicateCount = 1;
        for (int idx = 0; idx < battleParticipantsWithExpenditure.size(); idx++) {
            final BattleParticipantWithExpenditure current = battleParticipantsWithExpenditure.get(idx);
            final Long currentExpenditure = current.getExpenditure();

            if (idx == 0) {
                rank++;
            } else if (currentExpenditure.equals(prevExpenditure)) {
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
        return expenditureQueryService.getMembersTotalExpenditureRankBetween(
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

package com.poorlex.poorlex.battle.battle.service;

import com.poorlex.poorlex.alarm.battlealarm.service.BattleAlarmService;
import com.poorlex.poorlex.battle.battle.domain.*;
import com.poorlex.poorlex.battle.battle.service.dto.request.BattleCreateRequest;
import com.poorlex.poorlex.battle.battle.service.dto.request.BattleFindRequest;
import com.poorlex.poorlex.battle.battle.service.dto.response.*;
import com.poorlex.poorlex.battle.battle.service.event.BattleCreatedEvent;
import com.poorlex.poorlex.battle.participation.domain.BattleParticipant;
import com.poorlex.poorlex.battle.participation.domain.BattleParticipantRepository;
import com.poorlex.poorlex.battle.participation.domain.BattleParticipantRole;
import com.poorlex.poorlex.config.event.Events;
import com.poorlex.poorlex.consumption.expenditure.service.ExpenditureQueryService;
import com.poorlex.poorlex.consumption.expenditure.service.dto.RankAndTotalExpenditureDto;
import com.poorlex.poorlex.exception.ApiException;
import com.poorlex.poorlex.exception.ExceptionTag;
import com.poorlex.poorlex.user.member.domain.MemberLevel;
import com.poorlex.poorlex.user.member.service.MemberQueryService;
import com.poorlex.poorlex.user.member.service.dto.response.MyPageResponse;
import com.poorlex.poorlex.user.point.domain.Point;
import com.poorlex.poorlex.user.point.service.MemberPointQueryService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.poorlex.poorlex.user.point.service.dto.response.MyPageLevelInfoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
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
    private final MemberPointQueryService memberPointService;
    private final ExpenditureQueryService expenditureQueryService;
    private final MemberQueryService memberQueryService;
    private final BattleImageService imageService;
    private final String bucketDirectory;
    private final boolean activateStartTimeValidation;
    private final boolean activateEndTimeValidation;
    private final BattleQueryRepository battleQueryRepository;

    public BattleService(final BattleRepository battleRepository,
                         final BattleParticipantRepository battleParticipantRepository,
                         final BattleAlarmService battleAlarmService,
                         final MemberPointQueryService memberPointService,
                         final ExpenditureQueryService expenditureQueryService,
                         final MemberQueryService memberQueryService,
                         final BattleImageService imageService,
                         final BattleQueryRepository battleQueryRepository,
                         @Value("${aws.s3.battle-directory}") final String bucketDirectory,
                         @Value("${validation.start-time}") final boolean activateStartTimeValidation,
                         @Value("${validation.start-time}") final boolean activateEndTimeValidation) {
        this.battleRepository = battleRepository;
        this.battleParticipantRepository = battleParticipantRepository;
        this.battleAlarmService = battleAlarmService;
        this.memberPointService = memberPointService;
        this.expenditureQueryService = expenditureQueryService;
        this.memberQueryService = memberQueryService;
        this.imageService = imageService;
        this.bucketDirectory = bucketDirectory;
        this.battleQueryRepository = battleQueryRepository;
        this.activateStartTimeValidation = activateStartTimeValidation;
        this.activateEndTimeValidation = activateEndTimeValidation;
    }

    @Transactional
    public Long create(final Long memberId, final MultipartFile image, final BattleCreateRequest request) {
        validateMemberCanCreateBattle(memberId);
        final Battle battle = battleRepository.save(createBattle(image, request, BattleStatus.RECRUITING));

        Events.raise(new BattleCreatedEvent(battle.getId(), memberId));

        return battle.getId();
    }

    @Transactional
    public Long createProgressing(final Long memberId, final MultipartFile image, final BattleCreateRequest request) {
        validateMemberCanCreateBattle(memberId);
        final Battle battle = battleRepository.save(createBattle(image, request, BattleStatus.PROGRESS));

        Events.raise(new BattleCreatedEvent(battle.getId(), memberId));

        return battle.getId();
    }

    @Transactional
    public Long createCompleted(final Long memberId, final MultipartFile image, final BattleCreateRequest request) {
        validateMemberCanCreateBattle(memberId);
        final Battle battle = battleRepository.save(createBattle(image, request, BattleStatus.COMPLETE));

        Events.raise(new BattleCreatedEvent(battle.getId(), memberId));

        return battle.getId();
    }

    private void validateMemberCanCreateBattle(final Long memberId) {
        final int readiedBattleCount = battleRepository.countMemberBattleWithStatuses(
                memberId,
                BattleStatus.getReadiedStatues()
        );
        if (readiedBattleCount >= MAX_READIED_BATTLE_COUNT) {
            final String errorMessage = String.format("배틀은 최대 %d개까지 참여할 수 있습니다. ( 참가 중인 배틀 수 : %d )",
                                                      MAX_READIED_BATTLE_COUNT,
                                                      readiedBattleCount);
            throw new ApiException(ExceptionTag.BATTLE_PARTICIPATE, errorMessage);
        }
    }

    private Battle createBattle(final MultipartFile image,
                                final BattleCreateRequest request,
                                final BattleStatus battleStatus) {
        final BattleName name = new BattleName(request.getName());
        final BattleIntroduction introduction = new BattleIntroduction(request.getIntroduction());
        final BattleBudget budget = new BattleBudget(request.getBudget());
        final BattleParticipantSize participantSize = new BattleParticipantSize(request.getMaxParticipantSize());
        final String imageUrl = imageService.saveAndReturnPath(image, bucketDirectory);
        return Battle.withoutBattleId(name,
                                      introduction,
                                      new BattleImageUrl(imageUrl),
                                      budget,
                                      participantSize,
                                      BattleDuration.current(),
                                      battleStatus);
    }

    public List<MemberProgressBattleResponse> findProgressMemberBattles(final Long memberId, final LocalDate date) {
        final List<BattleWithMemberExpenditure> battles =
                battleRepository.findMemberBattlesByMemberIdAndStatusWithExpenditure(memberId, BattleStatus.PROGRESS);

        return battles.stream()
                .sorted(Comparator.comparing(battleInfo -> battleInfo.getBattle().getCreatedAt(),
                                             Comparator.reverseOrder()))
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
                getMemberRank(battle, memberId, battle.getNumberOfDayPassedAfterStart(date) + 1),
                battleParticipantCount,
                uncheckedAlarmCount
        );
    }

    private int getMemberRank(final Battle battle, final Long targetMemberId, final Long needExpenditureCount) {
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

            if (current.getExpenditureCount() < needExpenditureCount) {
                if (current.getBattleParticipant().getMemberId().equals(targetMemberId)) {
                    return 0;
                }
                continue;
            }

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

        final String errorMessage = String.format("배틀 참가자중에 해당 ID의 멤버가 존재하지 않습니다. ( 배틀 명 : '%s' , 멤버 ID : %d )",
                                                  battle.getName(),
                                                  targetMemberId);
        throw new ApiException(ExceptionTag.BATTLE_PARTICIPANT_FIND, errorMessage);
    }

    public List<MemberCompleteBattleResponse> findCompleteMemberBattles(final Long memberId, final LocalDate date) {
        final List<BattleWithMemberExpenditure> battles =
                battleRepository.findMemberBattlesByMemberIdAndStatusWithExpenditure(memberId, BattleStatus.COMPLETE);

        return battles.stream()
                .sorted(Comparator.comparing(battleInfo -> battleInfo.getBattle().getCreatedAt(),
                                             Comparator.reverseOrder()))
                .map(battleInfo -> mapToMemberCompleteBattleResponse(battleInfo, memberId, date))
                .toList();
    }

    private MemberCompleteBattleResponse mapToMemberCompleteBattleResponse(final BattleWithMemberExpenditure battleInfo,
                                                                           final Long memberId,
                                                                           final LocalDate date) {
        final Battle battle = battleInfo.getBattle();
        final int memberRank = getMemberRank(battle, memberId, Long.valueOf(BattleDuration.BATTLE_DAYS));
        final int battleParticipantCount = battleParticipantRepository.countBattleParticipantByBattleId(
                battle.getId());

        return MemberCompleteBattleResponse.from(battleInfo, date, memberRank, battleParticipantCount);
    }

    @Transactional
    public void startBattle(final Long battleId, final LocalDateTime current) {
        final Battle battle = findExistBattle(battleId);
        if (activateStartTimeValidation) {
            battle.start(current);
            return;
        }
        battle.startWithoutValidate();
    }

    private Battle findExistBattle(final Long battleId) {
        return battleRepository.findById(battleId)
                .orElseThrow(() -> {
                    final String errorMessage = String.format("ID에 해당하는 배틀이 존재하지 않습니다. ( ID : %d )", battleId);
                    return new ApiException(ExceptionTag.BATTLE_FIND, errorMessage);
                });
    }

    @Transactional
    public void endBattle(final Long battleId, final LocalDateTime current) {
        final Battle battle = findExistBattle(battleId);
        if (activateEndTimeValidation) {
            battle.end(current);
            return;
        }
        battle.endWithoutValidate();
    }

    public BattleResponse getBattleInfo(final Long battleId) {
        final Battle battle = findExistBattle(battleId);

        final List<BattleParticipant> participants = battleParticipantRepository.findAllByBattleId(battleId);

        Long managerId = participants.stream().filter(p -> p.getRole().equals(BattleParticipantRole.MANAGER))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("배틀 매니저를 찾을 수 없습니다."))
                .getMemberId();
        MyPageResponse managerInfo = memberQueryService.getMyPageInfoFromCurrentDatetime(managerId);

        MyPageLevelInfoResponse memberLevelInfo = memberPointService.findMemberLevelInfo(managerId);

        BattleManagerResponse managerResponse = new BattleManagerResponse(managerInfo.getNickname(), memberLevelInfo.getLevel(), managerInfo.getDescription());

        return new BattleResponse(battle, battle.getDDay(LocalDate.now()), managerResponse, participants.size());
    }

    private Map<Long, String> getParticipantsNickname(final List<Long> memberIds) {
        return memberQueryService.getMembersNickname(memberIds);
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
                .orElseThrow(() -> {
                    final String errorMessage = String.format("포인트에 해당하는 레벨이 존재하지 않습니다. ( 포인트 : %d )", totalPoint);
                    return new ApiException(ExceptionTag.MEMBER_LEVEL, errorMessage);
                })
                .getNumber();

        return new ParticipantRankingResponse(
                rankInfo.getRank(),
                level,
                battleParticipant.isManager(),
                nickname,
                rankInfo.getTotalExpenditure()
        );
    }

    public List<FindingBattleResponse> queryBattles(BattleFindRequest request, Pageable pageable) {
        return battleQueryRepository.queryBattles(request, pageable);
    }
}

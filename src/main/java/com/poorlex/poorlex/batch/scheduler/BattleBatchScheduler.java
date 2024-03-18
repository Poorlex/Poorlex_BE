package com.poorlex.poorlex.batch.scheduler;

import com.poorlex.poorlex.batch.config.BattleBatchConfig;
import com.poorlex.poorlex.batch.config.BattleSuccessBatchConfig;
import com.poorlex.poorlex.battle.domain.Battle;
import com.poorlex.poorlex.battle.domain.BattleDifficulty;
import com.poorlex.poorlex.battle.domain.BattleParticipantWithExpenditure;
import com.poorlex.poorlex.battle.domain.BattleRepository;
import com.poorlex.poorlex.battle.domain.BattleStatus;
import com.poorlex.poorlex.battlesuccess.domain.BattleSuccessHistory;
import com.poorlex.poorlex.battlesuccess.domain.BattleSuccessHistoryRepository;
import com.poorlex.poorlex.point.domain.MemberPoint;
import com.poorlex.poorlex.point.domain.MemberPointRepository;
import com.poorlex.poorlex.point.domain.Point;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class BattleBatchScheduler {

    private static final String REQUEST_DATE_TIME_KEY = "requestDateTime";
    private final JobLauncher jobLauncher;
    private final BattleBatchConfig battleBatchConfig;
    private final BattleSuccessBatchConfig battleSuccessBatchConfig;
    private final BattleRepository battleRepository;
    private final MemberPointRepository memberPointRepository;
    private final BattleSuccessHistoryRepository battleSuccessHistoryRepository;

    @Scheduled(cron = "${schedules.battle-start.cron}")
    @Transactional
    public void readiedBattleStart() {
        log.info("batch schedule run : {}", "준비 배틀 시작");
        battleRepository.findBattlesByStatusIn(BattleStatus.getReadiedStatues())
                .forEach(Battle::startWithoutValidate);
    }

    @Scheduled(cron = "${schedules.battle-end.cron}")
    @Transactional
    public void battlePointJob() {
        log.info("batch schedule run : {}", "배틀 성공 포인트 지급");

        battleRepository.findBattlesByStatusIn(List.of(BattleStatus.PROGRESS))
                .forEach(battle -> {
                    final Map<Long, Integer> battleRankings = getSuccessParticipantRankings(battle);
                    battleRankings.forEach((memberId, rank) -> saveBattlePoint(battle, memberId, rank));
                    battle.successPointSaved();
                });
    }

    private Map<Long, Integer> getSuccessParticipantRankings(final Battle battle) {
        final List<BattleParticipantWithExpenditure> participantsInfo =
                battleRepository.findBattleParticipantsWithExpenditureByBattleId(battle.getId()).stream()
                        .filter(participantInfo -> battle.isSuccess(participantInfo.getExpenditure()))
                        .sorted(Comparator.comparing(BattleParticipantWithExpenditure::getExpenditure))
                        .toList();

        final Map<Long, Integer> rankingMap = new HashMap<>();

        int rank = 0;
        Long prevExpenditure = -1L;

        for (final BattleParticipantWithExpenditure participantInfo : participantsInfo) {
            final Long currentExpenditure = participantInfo.getExpenditure();
            if (currentExpenditure > prevExpenditure) {
                rank++;
                prevExpenditure = currentExpenditure;
            }
            rankingMap.put(participantInfo.getBattleParticipant().getMemberId(), rank);
        }

        return rankingMap;
    }

    private void saveBattlePoint(final Battle battle, final Long memberId, final int rank) {
        final int score = battle.getBattleType().getScore(rank);
        memberPointRepository.save(MemberPoint.withoutId(new Point(score), memberId));
    }

    @Scheduled(cron = "${schedules.save-battle-success-history.cron}")
    @Transactional
    public void saveBattleSuccessHistoryAndEndBattle() {
        log.info("batch schedule run : {}", "배틀 성공 기록 저장 후 종료");
        final List<Battle> progressBattles = battleRepository.findBattlesByStatusIn(List.of(BattleStatus.PROGRESS));

        progressBattles.stream()
                .filter(Battle::isBattlePointPaid)
                .forEach(endableBattle -> {
                    battleRepository.findBattleParticipantsWithExpenditureByBattleId(endableBattle.getId())
                            .stream()
                            .filter(participantInfo -> endableBattle.isSuccess(participantInfo.getExpenditure()))
                            .forEach(successParticipant -> saveBattleSuccessHistory(endableBattle, successParticipant));
                    endableBattle.endWithoutValidate();
                });
    }

    private BattleSuccessHistory saveBattleSuccessHistory(final Battle endableBattle,
                                                          final BattleParticipantWithExpenditure successParticipant) {
        final Long memberId = successParticipant.getBattleParticipant().getMemberId();
        final Long battleId = endableBattle.getId();
        final BattleDifficulty battleDifficulty = endableBattle.getDifficulty();

        return battleSuccessHistoryRepository.save(BattleSuccessHistory.withoutId(memberId,
                                                                                  battleId,
                                                                                  battleDifficulty));
    }

    public void runBattleStartJob() {
        log.info("batch schedule run : {}", "battle start");

        final Map<String, JobParameter<?>> parameters = new HashMap<>();
        parameters.put(REQUEST_DATE_TIME_KEY, new JobParameter<>(LocalDateTime.now(), LocalDateTime.class));
        parameters.put("statuses",
                       new JobParameter<>(List.of(BattleStatus.RECRUITING, BattleStatus.RECRUITING_FINISHED),
                                          List.class));
        parameters.put("changeStatus", new JobParameter<>(BattleStatus.PROGRESS, BattleStatus.class));

        try {
            jobLauncher.run(battleBatchConfig.job(), new JobParameters(parameters));
        } catch (JobExecutionAlreadyRunningException |
                 JobRestartException |
                 JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            log.error(e.getMessage());
        }
    }


    public void runBattleEndJob() {
        log.info("batch schedule run : {}", "battle end");

        final Map<String, JobParameter<?>> parameters = new HashMap<>();
        parameters.put(REQUEST_DATE_TIME_KEY, new JobParameter<>(LocalDateTime.now(), LocalDateTime.class));
        parameters.put("statuses", new JobParameter<>(List.of(BattleStatus.PROGRESS), List.class));
        parameters.put("changeStatus", new JobParameter<>(BattleStatus.COMPLETE, BattleStatus.class));

        try {
            jobLauncher.run(battleBatchConfig.job(), new JobParameters(parameters));
        } catch (JobExecutionAlreadyRunningException |
                 JobRestartException |
                 JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            log.error(e.getMessage());
        }
    }

    public void saveBattleSuccessHistory() {
        log.info("batch schedule run : {}", "save battle success history");
        final Map<String, JobParameter<?>> parameters = new HashMap<>();
        parameters.put(REQUEST_DATE_TIME_KEY, new JobParameter<>(LocalDateTime.now(), LocalDateTime.class));

        try {
            jobLauncher.run(battleSuccessBatchConfig.job(), new JobParameters(parameters));
        } catch (JobExecutionAlreadyRunningException |
                 JobRestartException |
                 JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            log.error(e.getMessage());
        }
    }
}

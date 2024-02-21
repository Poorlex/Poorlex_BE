package com.poorlex.poorlex.batch.scheduler;

import com.poorlex.poorlex.batch.config.BattleBatchConfig;
import com.poorlex.poorlex.batch.config.BattleSuccessBatchConfig;
import com.poorlex.poorlex.battle.domain.BattleStatus;
import java.time.LocalDateTime;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class BattleBatchScheduler {

    private static final String REQUEST_DATE_TIME_KEY = "requestDateTime";
    private final JobLauncher jobLauncher;
    private final BattleBatchConfig battleBatchConfig;
    private final BattleSuccessBatchConfig battleSuccessBatchConfig;

    @Scheduled(cron = "${schedules.battle-start.cron}")
    public void runBattleStartJob() {
        log.info("batch schedule run : {}", "battle start");

        final Map<String, JobParameter<?>> parameters = new HashMap<>();
        parameters.put(REQUEST_DATE_TIME_KEY, new JobParameter<>(LocalDateTime.now(), LocalDateTime.class));
        parameters.put("statuses",
            new JobParameter<>(List.of(BattleStatus.RECRUITING, BattleStatus.RECRUITING_FINISHED), List.class));
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

    @Scheduled(cron = "${schedules.battle-end.cron}")
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

    @Scheduled(cron = "${schedules.save-battle-success-history.cron}")
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

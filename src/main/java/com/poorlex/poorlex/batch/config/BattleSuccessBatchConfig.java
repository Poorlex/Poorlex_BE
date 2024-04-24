package com.poorlex.poorlex.batch.config;

import com.poorlex.poorlex.battle.battle.domain.Battle;
import com.poorlex.poorlex.battle.succession.domain.BattleSuccessHistory;
import com.poorlex.poorlex.battle.participation.domain.BattleParticipant;
import jakarta.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BattleSuccessBatchConfig {

    private static final String JOB_NAME = "BATTLE_SUCCESS_";
    private static final String BEAN_PREFIX = JOB_NAME + "_";
    private static final int CHUNK_SIZE = 100;
    private static final String SAVE_SUCCESS_HISTORY = "saveSuccessHistory";
    private static final String BATTLE_SUCCESS_HISTORY_SAVED_CHECK = "battleSuccessHistorySavedCheck";

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final List<Long> handledBattleIds = new ArrayList<>();

    @Bean(JOB_NAME)
    public Job job() {
        return new JobBuilder(JOB_NAME, jobRepository)
                .start(saveSuccessHistoryStep())
                .next(battleSuccessHistorySavedCheckedStep())
                .build();
    }

    // 성공 기록 저장 Step
    @Bean(BEAN_PREFIX + "step_" + SAVE_SUCCESS_HISTORY)
    public Step saveSuccessHistoryStep() {
        return new StepBuilder(BEAN_PREFIX + "step", jobRepository)
                .<BattleParticipantWithBattleSumExpenditure, BattleSuccessHistory>chunk(CHUNK_SIZE,
                                                                                        platformTransactionManager)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    @Bean(BEAN_PREFIX + "reader_" + SAVE_SUCCESS_HISTORY)
    public JpaPagingItemReader<BattleParticipantWithBattleSumExpenditure> reader() {
        final String query = String.format(
                "select new %s(bp, b, coalesce(sum(e.amount.value), 0)) "
                        + "from Battle b "
                        + "left join BattleParticipant bp on b.id = bp.battleId "
                        + "left join Expenditure e on e.memberId = bp.memberId and "
                        + "e.date between cast(b.duration.start as LocalDate) and cast(b.duration.end as LocalDate) "
                        + "where b.status = 'PROGRESS' and b.isBattleSuccessCounted = false "
                        + "group by b.id"
                , BattleParticipantWithBattleSumExpenditure.class.getName());

        return new JpaPagingItemReaderBuilder<BattleParticipantWithBattleSumExpenditure>()
                .name(BEAN_PREFIX + "reader")
                .pageSize(CHUNK_SIZE)
                .entityManagerFactory(entityManagerFactory)
                .queryString(query)
                .build();
    }

    @Bean(BEAN_PREFIX + "processor_" + SAVE_SUCCESS_HISTORY)
    public ItemProcessor<BattleParticipantWithBattleSumExpenditure, BattleSuccessHistory> processor() {
        return battleSumExpenditure -> {
            final Battle battle = battleSumExpenditure.getBattle();
            final BattleParticipant battleParticipant = battleSumExpenditure.getBattleParticipant();
            final Long sumExpenditure = battleSumExpenditure.getSumExpenditure();
            handledBattleIds.add(battle.getId());
            if (battle.getBudgetLeft(sumExpenditure) >= 0) {
                return BattleSuccessHistory.withoutId(
                        battleParticipant.getMemberId(),
                        battleParticipant.getBattleId(),
                        battle.getDifficulty()
                );
            }
            return null;
        };
    }

    @Bean(BEAN_PREFIX + "writer_" + SAVE_SUCCESS_HISTORY)
    public JpaItemWriter<BattleSuccessHistory> writer() {
        return new JpaItemWriterBuilder<BattleSuccessHistory>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    // 성공 기록 저장 여부 변경 Step
    @Bean(BEAN_PREFIX + "step_" + BATTLE_SUCCESS_HISTORY_SAVED_CHECK)
    public Step battleSuccessHistorySavedCheckedStep() {
        handledBattleIds.clear();
        return new StepBuilder(BEAN_PREFIX + "step", jobRepository)
                .<Battle, Battle>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(handledBattleReader())
                .processor(handleBattleProcessor())
                .writer(handleBattleWriter())
                .build();
    }

    @Bean(BEAN_PREFIX + "reader_" + BATTLE_SUCCESS_HISTORY_SAVED_CHECK)
    public JpaPagingItemReader<Battle> handledBattleReader() {
        final String query = "select b from Battle b where b.id in :ids and b.isBattleSuccessCounted = false";
        final Map<String, Object> params = new HashMap<>();
        params.put("ids", handledBattleIds);

        return new JpaPagingItemReaderBuilder<Battle>()
                .name(BEAN_PREFIX + "reader")
                .pageSize(CHUNK_SIZE)
                .entityManagerFactory(entityManagerFactory)
                .queryString(query)
                .parameterValues(params)
                .build();
    }

    @Bean(BEAN_PREFIX + "processor_" + BATTLE_SUCCESS_HISTORY_SAVED_CHECK)
    public ItemProcessor<Battle, Battle> handleBattleProcessor() {
        return battle -> {
            battle.successPointSaved();
            return battle;
        };
    }

    @Bean(BEAN_PREFIX + "writer_" + BATTLE_SUCCESS_HISTORY_SAVED_CHECK)
    public JpaItemWriter<Battle> handleBattleWriter() {
        return new JpaItemWriterBuilder<Battle>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }
}


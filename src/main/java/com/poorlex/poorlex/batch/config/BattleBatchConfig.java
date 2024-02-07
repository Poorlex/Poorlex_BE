package com.poorlex.poorlex.batch.config;

import com.poorlex.poorlex.batch.jobparameter.BattleJobParameter;
import com.poorlex.poorlex.battle.domain.Battle;
import com.poorlex.poorlex.battle.domain.BattleStatus;
import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
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
public class BattleBatchConfig {

    private static final String JOB_NAME = "BATTLE_";
    private static final String BEAN_PREFIX = JOB_NAME + "_";
    private static final int CHUNK_SIZE = 100;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final BattleJobParameter battleJobParameter;

    @Bean
    @JobScope
    public BattleJobParameter battleJobParameter() {
        return new BattleJobParameter();
    }

    @Bean(JOB_NAME)
    public Job job() {
        return new JobBuilder(JOB_NAME, jobRepository)
            .start(step())
            .build();
    }

    @Bean(BEAN_PREFIX + "step")
    @JobScope
    public Step step() {
        return new StepBuilder(BEAN_PREFIX + "step", jobRepository)
            .<Battle, Battle>chunk(CHUNK_SIZE, platformTransactionManager)
            .reader(reader())
            .processor(processor())
            .writer(writer())
            .build();
    }

    @Bean(BEAN_PREFIX + "reader")
    @StepScope
    public JpaPagingItemReader<Battle> reader() {
        final String namedQuery = "select b from Battle b where b.status in :statuses";
        final Map<String, Object> params = new HashMap<>();
        params.put("statuses", battleJobParameter.getStatuses());
        log.info("statuses = {}", battleJobParameter.getStatuses());

        return new JpaPagingItemReaderBuilder<Battle>()
            .name(BEAN_PREFIX + "reader")
            .pageSize(CHUNK_SIZE)
            .entityManagerFactory(entityManagerFactory)
            .queryString(namedQuery)
            .parameterValues(params)
            .build();
    }

    @Bean(BEAN_PREFIX + "processor")
    @StepScope
    public ItemProcessor<Battle, Battle> processor() {
        return battle -> {
            final BattleStatus changeStatus = battleJobParameter.getChangeStatus();
            final LocalDateTime requestDateTime = battleJobParameter.getRequestDateTime();
            log.info("changeStatus = {}", changeStatus);
            log.info("requestDateTime = {}", requestDateTime);
            if (changeStatus == BattleStatus.PROGRESS) {
                battle.start(requestDateTime);
            }
            if (changeStatus == BattleStatus.COMPLETE) {
                battle.end(requestDateTime);
            }
            return battle;
        };
    }

    @Bean(BEAN_PREFIX + "writer")
    public JpaItemWriter<Battle> writer() {
        return new JpaItemWriterBuilder<Battle>()
            .entityManagerFactory(entityManagerFactory)
            .build();
    }
}

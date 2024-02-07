package com.poorlex.poorlex.batch.jobparameter;

import com.poorlex.poorlex.battle.domain.BattleStatus;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;

@Getter
public class BattleJobParameter {

    @Value("#{jobParameters[statuses]}")
    private List<BattleStatus> statuses;

    @Value("#{jobParameters[changeStatus]}")
    private BattleStatus changeStatus;

    @Value("#{jobParameters[requestDateTime]}")
    private LocalDateTime requestDateTime;

    @Value("#{jobParameters[subTitle]}")
    private String subTitle;

}

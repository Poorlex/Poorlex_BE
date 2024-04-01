package com.poorlex.poorlex.bridge;

import com.poorlex.poorlex.battle.domain.Battle;
import com.poorlex.poorlex.battle.domain.BattleRepository;
import com.poorlex.poorlex.consumption.expenditure.service.dto.BattleDurationDto;
import com.poorlex.poorlex.consumption.expenditure.service.provider.BattleDurationProvider;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BattleDurationProviderImpl implements BattleDurationProvider {

    private final BattleRepository battleRepository;

    @Override
    public BattleDurationDto getDurationById(final Long battleId) {
        final Battle battle = battleRepository.findById(battleId)
                .orElseThrow(IllegalArgumentException::new);
        final LocalDate startDate = LocalDate.from(battle.getDuration().getStart());
        final LocalDate endDate = LocalDate.from(battle.getDuration().getEnd());
        
        return new BattleDurationDto(startDate, endDate);
    }
}

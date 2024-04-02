package com.poorlex.poorlex.consumption.expenditure.service.provider;

import com.poorlex.poorlex.consumption.expenditure.service.dto.BattleDurationDto;

public interface BattleDurationProvider {

    BattleDurationDto getDurationById(Long battleId);
}

package com.poorlex.poorlex.battle.domain;

import com.poorlex.poorlex.exception.ApiException;
import com.poorlex.poorlex.exception.ExceptionTag;
import java.util.Arrays;
import java.util.List;

public enum BattleStatus {
    RECRUITING,
    RECRUITING_FINISHED,
    PROGRESS,
    COMPLETE;

    public static BattleStatus findByName(final String statusName) {
        return Arrays.stream(values())
                .filter(status -> status.name().equals(statusName.toUpperCase()))
                .findFirst()
                .orElseThrow(() -> {
                    final String errorMessage = String.format("이름에 해당하는 배틀 상태가 존재하지 않습니다. ( 입력 : '%s')", statusName);
                    return new ApiException(ExceptionTag.BATTLE_STATUS, errorMessage);
                });
    }

    public static List<BattleStatus> getReadiedStatues() {
        return List.of(BattleStatus.RECRUITING, BattleStatus.RECRUITING_FINISHED);
    }
}

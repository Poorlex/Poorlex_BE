package com.poorlex.poorlex.battle.battle.service.dto.request;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.poorlex.poorlex.battle.battle.domain.BattleStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BattleFindRequest {

    private LocalDate date;
    private List<BattleStatus> status = new ArrayList<>();
}

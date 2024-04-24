package com.poorlex.poorlex.battle.battle.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class BattleCreateRequest {

    private String name;
    private String introduction;
    private int budget;
    private int maxParticipantSize;
}

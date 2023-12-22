package com.poolex.poolex.battle.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class BattleCreateRequest {

    private String name;
    private String introduction;
    private String imageUrl;
    private int budget;
    private int maxParticipantSize;
}

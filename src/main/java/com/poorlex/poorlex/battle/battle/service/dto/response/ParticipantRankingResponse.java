package com.poorlex.poorlex.battle.battle.service.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ParticipantRankingResponse {

    private final int rank;
    private final int level;
    private final boolean isManager;
    private final String nickname;
    private final Long expenditure;
}

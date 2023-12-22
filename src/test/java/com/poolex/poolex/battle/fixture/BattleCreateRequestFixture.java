package com.poolex.poolex.battle.fixture;

import com.poolex.poolex.battle.service.dto.request.BattleCreateRequest;

public class BattleCreateRequestFixture {

    private BattleCreateRequestFixture() {

    }

    public static BattleCreateRequest create(final String name,
                                             final String introduction,
                                             final String imageUrl,
                                             final int budget,
                                             final int maxParticipantSize) {
        return new BattleCreateRequest(name, introduction, imageUrl, budget, maxParticipantSize);
    }

    public static BattleCreateRequest simple() {
        return create("name", "introduction", "imageUrl", 10000, 10);
    }
}

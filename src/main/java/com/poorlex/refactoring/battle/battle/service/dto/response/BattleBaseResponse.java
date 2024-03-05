package com.poorlex.refactoring.battle.battle.service.dto.response;

import com.poorlex.refactoring.battle.battle.domain.Battle;

public class BattleBaseResponse implements BattleResponse {

    private final Long id;
    private final String name;
    private final String imageUrl;
    private final String difficulty;
    private final Long budget;
    private final int battleParticipantCount;

    public BattleBaseResponse(final Long id,
                              final String name,
                              final String imageUrl,
                              final String difficulty,
                              final Long budget,
                              final int battleParticipantCount) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.difficulty = difficulty;
        this.budget = budget;
        this.battleParticipantCount = battleParticipantCount;
    }

    public BattleBaseResponse(final Battle battle) {
        this(battle.getId(),
            battle.getName(),
            battle.getImageUrl(),
            battle.getDifficulty(),
            battle.getBudget(),
            battle.getBattleParticipantLimit()
        );
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public Long getBudget() {
        return budget;
    }

    public int getBattleParticipantCount() {
        return battleParticipantCount;
    }
}

package com.poorlex.poorlex.battle.battle.service.dto.response;

import com.poorlex.poorlex.battle.battle.domain.Battle;
import com.poorlex.poorlex.battle.battle.domain.BattleBudget;
import com.poorlex.poorlex.battle.battle.domain.BattleWithCurrentParticipantSize;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class FindingBattleResponse {

    private final Long battleId;
    private final String name;
    private final String introduction;
    private final String imageUrl;
    private final String difficulty;
    private final int budget;
    private final int currentParticipant;
    private final int maxParticipantCount;

    public static List<FindingBattleResponse> parseToList(final List<BattleWithCurrentParticipantSize> battles) {
        return battles.stream()
                .map(FindingBattleResponse::from)
                .toList();
    }

    public static FindingBattleResponse from(final BattleWithCurrentParticipantSize battleWithCurrentParticipantSize) {
        final Battle battle = battleWithCurrentParticipantSize.getBattle();
        final int currentParticipantSize = battleWithCurrentParticipantSize.getCurrentParticipantSize();

        return new FindingBattleResponse(
                battle.getId(),
                battle.getName(),
                battle.getIntroduction(),
                battle.getImageUrl(),
                battle.getDifficulty().name(),
                battle.getBudget(),
                currentParticipantSize,
                battle.getMaxParticipantSize().getValue()
        );
    }

    public FindingBattleResponse(Long battleId, String name, String introduction, String imageUrl, int budget, int maxParticipantCount, int currentParticipant) {
        this.battleId = battleId;
        this.name = name;
        this.introduction = introduction;
        this.imageUrl = imageUrl;
        this.budget = budget;
        BattleBudget battleBudget = new BattleBudget(budget);
        this.difficulty = battleBudget.getDifficulty().name();
        this.maxParticipantCount = maxParticipantCount;
        this.currentParticipant = currentParticipant;
    }
}

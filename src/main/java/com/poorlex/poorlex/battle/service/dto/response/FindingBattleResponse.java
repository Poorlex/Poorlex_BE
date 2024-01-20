package com.poorlex.poorlex.battle.service.dto.response;

import com.poorlex.poorlex.battle.domain.Battle;
import com.poorlex.poorlex.battle.domain.BattleWithCurrentParticipantSize;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class FindingBattleResponse {

    private final Long battleId;
    private final String name;
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
            battle.getImageUrl(),
            battle.getDifficulty().name(),
            battle.getBudget(),
            currentParticipantSize,
            battle.getMaxParticipantSize().getValue()
        );
    }
}

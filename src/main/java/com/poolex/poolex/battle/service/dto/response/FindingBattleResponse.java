package com.poolex.poolex.battle.service.dto.response;

import com.poolex.poolex.battle.domain.Battle;
import com.poolex.poolex.battle.domain.BattleWithCurrentParticipantSize;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class FindingBattleResponse {

    private final String name;
    private final String imageUrl;
    private final String difficulty;
    private final long dDay;
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
            battle.getName(),
            battle.getImageUrl(),
            battle.getDifficulty().name(),
            getDDay(battle.getDuration().getEnd()),
            currentParticipantSize,
            battle.getMaxParticipantSize().getValue()
        );
    }

    private static long getDDay(final LocalDateTime end) {
        return ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.from(end));
    }
}

package com.poorlex.poorlex.battlealarmreaction.service.mapper;

import com.poorlex.poorlex.battlealarmreaction.domain.AlarmReaction;
import com.poorlex.poorlex.battlealarmreaction.service.dto.response.AlarmReactionResponse;
import java.time.LocalDateTime;

public class AlarmReactionResponseMapper {

    private AlarmReactionResponseMapper() {

    }

    public static AlarmReactionResponse mapToResponse(final AlarmReaction alarmReaction) {
        final String type = alarmReaction.getType().name();
        final String content = alarmReaction.getContent().getValue();
        final LocalDateTime createdAt = alarmReaction.getCreatedAt();

        return new AlarmReactionResponse(type, content, createdAt);
    }
}

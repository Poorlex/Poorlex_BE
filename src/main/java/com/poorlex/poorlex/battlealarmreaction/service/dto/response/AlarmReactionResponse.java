package com.poorlex.poorlex.battlealarmreaction.service.dto.response;

import com.poorlex.poorlex.battlealarmreaction.domain.AlarmReaction;
import com.poorlex.poorlex.common.AbstractCreatedAtResponse;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class AlarmReactionResponse extends AbstractCreatedAtResponse {

    private final String type;
    private final String content;

    public AlarmReactionResponse(final String type, final String content, final LocalDateTime createdAt) {
        super(createdAt);
        this.type = type;
        this.content = content;
    }

    public static AlarmReactionResponse from(final AlarmReaction alarmReaction) {
        return new AlarmReactionResponse(
            alarmReaction.getType().name(),
            alarmReaction.getContent().getValue(),
            alarmReaction.getCreatedAt()
        );
    }
}

package com.poorlex.poorlex.battlealarmreaction.service.dto.response;

import com.poorlex.poorlex.alarm.battlealarm.service.dto.response.AbstractBattleAlarmResponse;
import com.poorlex.poorlex.battlealarmreaction.domain.AlarmReaction;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class AlarmReactionResponse extends AbstractBattleAlarmResponse {

    private static final String ALARM_TYPE = "ALARM_REACTION";

    private final String alarmReactionType;
    private final String alarmReactionContent;

    public AlarmReactionResponse(final String alarmReactionType,
                                 final String alarmReactionContent,
                                 final LocalDateTime createdAt) {
        super(ALARM_TYPE, createdAt);
        this.alarmReactionType = alarmReactionType;
        this.alarmReactionContent = alarmReactionContent;
    }

    public static AlarmReactionResponse from(final AlarmReaction alarmReaction) {
        return new AlarmReactionResponse(
            alarmReaction.getType().name(),
            alarmReaction.getContent().getValue(),
            alarmReaction.getCreatedAt()
        );
    }
}

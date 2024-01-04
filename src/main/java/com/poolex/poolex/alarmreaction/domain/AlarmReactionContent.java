package com.poolex.poolex.alarmreaction.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AlarmReactionContent {

    private static final Pattern ALARM_REACTION_CONTENT_REGEX = Pattern.compile("[가-힣]+");

    private static final int MAX_LENGTH = 10;
    @Column(name = "content")
    private String value;

    public AlarmReactionContent(final String value) {
        validate(value.strip());
        this.value = value.strip();
    }

    private void validate(final String value) {
        if (!StringUtils.hasText(value)) {
            throw new IllegalArgumentException();
        }
        if (!ALARM_REACTION_CONTENT_REGEX.matcher(value).matches()) {
            throw new IllegalArgumentException();
        }
        validateLength(value);
    }

    private void validateLength(final String value) {
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException();
        }
    }

    public String getValue() {
        return value;
    }
}

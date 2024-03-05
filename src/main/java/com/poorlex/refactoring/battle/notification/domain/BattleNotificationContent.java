package com.poorlex.refactoring.battle.notification.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BattleNotificationContent {

    private static final int MIN_LENGTH = 20;
    private static final int MAX_LENGTH = 200;
    @Column(name = "content")
    private String value;

    public BattleNotificationContent(final String value) {
        validate(value.strip());
        this.value = value.strip();
    }

    private void validate(final String value) {
        if (!StringUtils.hasText(value)) {
            throw new IllegalArgumentException();
        }
        validateLength(value);
    }

    private void validateLength(final String value) {
        if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException();
        }
    }

    public String getValue() {
        return value;
    }
}

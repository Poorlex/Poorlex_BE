package com.poolex.poolex.battle.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BattleIntroduction {

    private static final int MINIMUM_NAME_LENGTH = 2;
    private static final int MAXIMUM_NAME_LENGTH = 200;
    @Column(name = "introduction")
    private String value;

    public BattleIntroduction(final String value) {
        validate(value);
        this.value = value;
    }

    private void validate(final String value) {
        if (!StringUtils.hasText(value)) {
            throw new IllegalArgumentException();
        }
        validateLength(value.length());
    }

    private void validateLength(final int length) {
        if (MINIMUM_NAME_LENGTH > length || length > MAXIMUM_NAME_LENGTH) {
            throw new IllegalArgumentException();
        }
    }

    public String getValue() {
        return value;
    }
}

package com.poorlex.poorlex.battle.domain;

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
    @Column(name = "introduction", nullable = false)
    private String value;

    public BattleIntroduction(final String value) {
        validate(value.strip());
        this.value = value.strip();
    }

    private void validate(final String value) {
        if (!StringUtils.hasText(value)) {
            throw new IllegalArgumentException("배틀 소개 내용이 없습니다.");
        }
        validateLength(value.length());
    }

    private void validateLength(final int length) {
        if (MINIMUM_NAME_LENGTH > length || length > MAXIMUM_NAME_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("배틀 소개는 %d자 이상 %d자 이하여야 합니다. ( 입력값 : %d )", MINIMUM_NAME_LENGTH, MAXIMUM_NAME_LENGTH, length)
            );
        }
    }

    public String getValue() {
        return value;
    }
}

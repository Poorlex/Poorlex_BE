package com.poorlex.poorlex.battle.domain;

import com.poorlex.poorlex.exception.ApiException;
import com.poorlex.poorlex.exception.ExceptionTag;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BattleName {

    private static final int MINIMUM_NAME_LENGTH = 2;
    private static final int MAXIMUM_NAME_LENGTH = 12;
    @Column(name = "name", nullable = false)
    private String value;

    public BattleName(final String value) {
        validate(value.strip());
        this.value = value.strip();
    }

    private void validate(final String value) {
        if (!StringUtils.hasText(value)) {
            final String errorMessage = "배틀 명이 비어있습니다.";
            throw new ApiException(ExceptionTag.BATTLE_NAME, errorMessage);
        }
        validateLength(value.length());
    }

    private void validateLength(final int length) {
        if (MINIMUM_NAME_LENGTH > length || length > MAXIMUM_NAME_LENGTH) {
            final String errorMessage = String.format("배틀명은 %d자 이상 %d자 이하여야 합니다. ( 입력한 길이 : %d )",
                                                      MINIMUM_NAME_LENGTH,
                                                      MAXIMUM_NAME_LENGTH,
                                                      length);
            throw new ApiException(ExceptionTag.BATTLE_NAME, errorMessage);
        }
    }

    public String getValue() {
        return value;
    }
}

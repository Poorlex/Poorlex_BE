package com.poorlex.poorlex.expenditure.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExpenditureDescription {

    private static final int MINIMUM_NAME_LENGTH = 1;
    private static final int MAXIMUM_NAME_LENGTH = 30;
    @Column(name = "description")
    private String value;

    public ExpenditureDescription(final String value) {
        validate(value.strip());
        this.value = value.strip();
    }

    private static void validate(final String value) {
        if (!StringUtils.hasText(value)) {
            throw new IllegalArgumentException("지출 설명이 비어있습니다.");
        }
        validateLength(value.length());
    }

    private static void validateLength(final int length) {
        if (MINIMUM_NAME_LENGTH > length || length > MAXIMUM_NAME_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("지출 설명은 %d자 이상 %d자 이하여야 합니다. ( 입력 길이 : %d )", MINIMUM_NAME_LENGTH, MAXIMUM_NAME_LENGTH, length)
            );
        }
    }

    public String getValue() {
        return value;
    }
}

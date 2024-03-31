package com.poorlex.poorlex.consumption.expenditure.domain;

import com.poorlex.poorlex.exception.ApiException;
import com.poorlex.poorlex.exception.ExceptionTag;
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
            throw new ApiException(ExceptionTag.EXPENDITURE_DESCRIPTION, "지출 설명이 비어있습니다.");
        }
        validateLength(value);
    }

    private static void validateLength(final String value) {
        final int length = value.length();
        if (MINIMUM_NAME_LENGTH > length || length > MAXIMUM_NAME_LENGTH) {
            final String errorMessage = String.format("지출 설명은 %d자 이상 %d자 이하여야 합니다. ( 입력 설명 : '%s', 입력 길이 : %d )",
                                                      MINIMUM_NAME_LENGTH,
                                                      MAXIMUM_NAME_LENGTH,
                                                      value,
                                                      length);
            throw new ApiException(ExceptionTag.EXPENDITURE_DESCRIPTION, errorMessage);
        }
    }

    public String getValue() {
        return value;
    }
}

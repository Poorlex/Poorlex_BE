package com.poorlex.poorlex.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberDescription {

    @Column(name = "description")
    private String value;

    private static final int MINIMUM_DESCRIPTION_LENGTH = 2;
    private static final int MAXIMUM_DESCRIPTION_LENGTH = 300;

    public MemberDescription(final String value) {
        validate(value.strip());
        this.value = value.strip();
    }

    private void validate(final String value) {
        if (!StringUtils.hasText(value)) {
            throw new IllegalArgumentException();
        }
        validateLength(value.length());
    }

    private void validateLength(final int length) {
        if (MINIMUM_DESCRIPTION_LENGTH > length || length > MAXIMUM_DESCRIPTION_LENGTH) {
            throw new IllegalArgumentException();
        }
    }

    public String getValue() {
        return value;
    }
}

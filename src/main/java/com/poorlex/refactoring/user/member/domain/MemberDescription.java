package com.poorlex.refactoring.user.member.domain;

import com.poorlex.refactoring.user.member.service.validation.exception.MemberException;
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
        try {
            validate(value);
        } catch (IllegalArgumentException e) {
            throw new MemberException.NotUpdatableDescriptionException();
        }
        this.value = value.strip();
    }

    private void validate(final String value) {
        final String validCharacters = value.strip();
        if (!StringUtils.hasText(validCharacters)) {
            throw new IllegalArgumentException();
        }
        validateLength(validCharacters.length());
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

package com.poolex.poolex.login.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberNickname {

    private static final Pattern NICKNAME_PATTERN = Pattern.compile("[가-힣a-zA-Z0-9_-]+");
    private static final int MINIMUM_LENGTH = 2;
    private static final int MAXIMUM_LENGTH = 15;
    @Column(name = "nickname")
    private String value;

    public MemberNickname(final String value) {
        validate(value.strip());
        this.value = value.strip();
    }

    private void validate(final String value) {
        if (!StringUtils.hasText(value)) {
            throw new IllegalArgumentException();
        }
        validateLength(value.length());
        validateValidCharacters(value);
    }

    private void validateLength(final int length) {
        if (MINIMUM_LENGTH > length || length > MAXIMUM_LENGTH) {
            throw new IllegalArgumentException();
        }
    }

    private void validateValidCharacters(final String value) {
        if (!NICKNAME_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException();
        }
    }

    public String getValue() {
        return value;
    }
}

package com.poorlex.refactoring.user.member.domain;

import com.poorlex.refactoring.user.member.service.validation.exception.MemberException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberNickname {

    private static final Pattern NICKNAME_PATTERN = Pattern.compile("[ 가-힣a-zA-Z0-9_-]+");
    private static final int MINIMUM_LENGTH = 2;
    private static final int MAXIMUM_LENGTH = 15;
    @Column(name = "nickname")
    private String value;

    public MemberNickname(final String value) {
        try {
            validate(value);
        } catch (IllegalArgumentException e) {
            throw new MemberException.NotUpdatableNicknameException();
        }
        this.value = value.strip();
    }

    private void validate(final String value) {
        final String validCharacters = value.strip();
        if (!StringUtils.hasText(validCharacters)) {
            throw new IllegalArgumentException();
        }
        validateLength(validCharacters.length());
        validateValidCharacters(validCharacters);
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

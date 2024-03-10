package com.poorlex.poorlex.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberNickname {

    private static final Pattern NICKNAME_PATTERN = Pattern.compile("[ 가-힣a-zA-Z0-9_-]+");
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
            throw new IllegalArgumentException("닉네임은 비어있거나 공백만으로 이루어질 수 없습니다.");
        }
        validateLength(value.length());
        validateValidCharacters(value);
    }

    private void validateLength(final int length) {
        if (MINIMUM_LENGTH > length || length > MAXIMUM_LENGTH) {
            throw new IllegalArgumentException(
                String.format("회원 닉네임은 %d자 이상 %d자 이하여야 합니다. ( 입력 길이 : %d )", MINIMUM_LENGTH, MAXIMUM_LENGTH, length)
            );
        }
    }

    private void validateValidCharacters(final String value) {
        if (!NICKNAME_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException(
                String.format("닉네임은 한글, 영어, 숫자, 특수기호( -, _ )만 사용할 수 있습니다. ( 입력 닉네임 : %s)", value)
            );
        }
    }

    public String getValue() {
        return value;
    }
}

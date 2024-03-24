package com.poorlex.poorlex.member.domain;

import com.poorlex.poorlex.exception.ApiException;
import com.poorlex.poorlex.exception.ExceptionTag;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberNickname {

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
            final String errorMessage = String.format("닉네임은 비어있거나 공백만으로 이루어질 수 없습니다. ( 입력값 : %s )", value);
            throw new ApiException(ExceptionTag.MEMBER_NICKNAME, errorMessage);
        }
        validateLength(value);
    }

    private void validateLength(final String nickname) {
        final int length = nickname.length();
        if (MINIMUM_LENGTH > length || length > MAXIMUM_LENGTH) {
            final String errorMessage = String.format("회원 닉네임은 %d자 이상 %d자 이하여야 합니다. ( 입력 값 : %s, 입력 길이 : %d )",
                                                      MINIMUM_LENGTH,
                                                      MAXIMUM_LENGTH,
                                                      nickname,
                                                      length);
            throw new ApiException(ExceptionTag.MEMBER_NICKNAME, errorMessage);
        }
    }

    public String getValue() {
        return value;
    }
}

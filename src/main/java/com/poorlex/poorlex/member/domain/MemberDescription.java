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
            final String errorMessage = String.format("회원 소개는 비어있거나 공백만으로 이루어질 수 없습니다. ( 소개 : '%s' )", value);
            throw new ApiException(ExceptionTag.MEMBER_INTRODUCTION, errorMessage);
        }
        validateLength(value);
    }

    private void validateLength(final String value) {
        final int length = value.length();
        if (MINIMUM_DESCRIPTION_LENGTH > length || length > MAXIMUM_DESCRIPTION_LENGTH) {
            final String errorMessage = String.format("회원 소개는 %d자 이상 %d자 이하여야 합니다. ( 입력 소개 : '%s' 입력 길이 : %d )",
                                                      MINIMUM_DESCRIPTION_LENGTH,
                                                      MAXIMUM_DESCRIPTION_LENGTH,
                                                      value,
                                                      length);
            throw new ApiException(ExceptionTag.MEMBER_INTRODUCTION, errorMessage);
        }
    }

    public String getValue() {
        return value;
    }
}

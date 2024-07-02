package com.poorlex.poorlex.user.member.domain;

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

    private static final int MAXIMUM_DESCRIPTION_LENGTH = 300;

    public MemberDescription(final String value) {
        validateLength(value.strip());
        this.value = value.strip();
    }

    private void validateLength(final String value) {
        final int length = value.length();
        if (length > MAXIMUM_DESCRIPTION_LENGTH) {
            final String errorMessage = String.format("회원 소개는 %d자 이하여야 합니다. ( 입력 소개 : '%s' 입력 길이 : %d )",
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

package com.poorlex.poorlex.battle.battle.domain;

import com.poorlex.poorlex.exception.ApiException;
import com.poorlex.poorlex.exception.ExceptionTag;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BattleImageUrl {

    @Column(name = "image_url", columnDefinition = "text", nullable = false)
    private String value;

    public BattleImageUrl(final String value) {
        validate(value);
        this.value = value;
    }

    private void validate(final String value) {
        if (!StringUtils.hasText(value)) {
            final String errorMessage = "배틀 이미지 URL 이 비어있습니다.";
            throw new ApiException(ExceptionTag.BATTLE_IMAGE, errorMessage);

        }
    }

    public String getValue() {
        return value;
    }
}
